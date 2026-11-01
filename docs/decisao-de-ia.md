# Decisão de IA — por que híbrido, e não "um LLM"

## O problema de negócio

A jornada de cuidado do pet é **reativa**: o tutor só age quando o animal já está visivelmente doente. No Sprint 2 provamos que a telemetria existe (temperatura, FC, SpO₂ a cada 2 s), mas dado bruto não muda comportamento — o único mecanismo de decisão era este trecho de `iot_sensor_simulation.py`:

```python
if not (r["temp"][0] <= t <= r["temp"][1]):
    alerts.append(f"Temperatura fora do normal: {t}°C")
```

Um threshold populacional fixo tem três limitações:

1. **Só dispara quando já saiu do normal da espécie** — um cão cujo baseline é 38,2 °C pode subir 0,9 °C (sinal clinicamente relevante) e continuar "normal" (39,1 °C).
2. **É binário e sem contexto** — não sabe que a vacina venceu, que o tutor não confirma o remédio, que o pet é sênior.
3. **Não diz o que fazer** — gera alarme, não ação.

O componente de IA do Sprint 3 — **CLYVO Copilot** — resolve exatamente isso: **aprende o normal de cada pet, calcula risco, decide a próxima melhor ação por protocolo e comunica em linguagem humana**, priorizando a fila da clínica.

| Ator | Valor entregue |
|---|---|
| Tutor | Sabe *o que fazer* e *quando*, em português simples, no canal e horário em que responde |
| Clínica | Fila de triagem ordenada por risco; agenda preenchida com ações preventivas; receita previsível |
| Pet | Desvio detectado dias antes do sintoma clínico evidente |

## Abordagens consideradas

| Abordagem | Prós | Contras | Veredito |
|---|---|---|---|
| **Só LLM** ("manda tudo pro modelo e pergunta o que fazer") | Rápido de prototipar; linguagem natural | Não determinístico; caro por leitura; impossível medir com matriz de confusão; alucina protocolo clínico; não auditável | ❌ como decisor |
| **Só regras** (threshold + protocolo) | Auditável, barato, determinístico | Não aprende o normal individual; não combina sinais fracos; não personaliza comunicação | ❌ sozinho |
| **Só modelo preditivo** | Combina sinais; calibrado; explicável | Cold start (sem rótulos no dia 1); não sabe redigir; não substitui protocolo | ❌ sozinho |
| **Híbrido em camadas** — regras + preditivo + recomendação + LLM só para redigir | Cada técnica no papel em que é forte; decisão auditável; comunicação natural; degrada graciosamente | Mais componentes para manter | ✅ **adotado** |

**Princípio central: decisão é determinística e auditável; expressão é generativa.** O LLM nunca decide; ele redige sobre uma decisão já tomada e persistida.

## As camadas

### C1 — Baseline individual (feature engineering)
- Para cada métrica: EWMA (média e desvio exponencialmente ponderados) sobre o histórico do **próprio pet**, excluindo a janela recente (10 leituras).
- Features: `*_z` (desvio da janela recente em σ do baseline) e `*_slope` (tendência linear na janela, em σ).
- **Cold start explícito:** com < 15 leituras usa o prior da espécie; entre 1 e 15 mistura prior e histórico (`blended`); só depois passa a `individual`. A fonte do baseline é registrada e exibida no dashboard.
- Features clínicas: idade, peso, espécie, dias desde a última consulta, dias de atraso vacinal, adesão a medicamentos.

### C2 — Modelo preditivo (scikit-learn)
- **IsolationForest** (não supervisionado) treinado só com telemetria normal: pontua o quanto a janela recente foge do "normal aprendido". Funciona sem rótulos.
- **RandomForest** (supervisionado, 300 árvores, `class_weight=balanced`) estima `P(precisa de atenção veterinária em 7 dias)`. Comparado com **Regressão Logística** como baseline.
- `risk_score = 100 × (0,65 · P + 0,35 · anomalia)`; níveis: Baixo < 30 ≤ Moderado < 60 ≤ Alto < 80 ≤ Crítico.
- Explicabilidade: `feature_importance × magnitude normalizada` → "fatores que mais pesaram", exibidos ao veterinário.
- **Dados de treino sintéticos, com seed fixa** (`data/generate_dataset.py`): 2.000 episódios gerados a partir de um modelo latente (idade, atraso vacinal, adesão → probabilidade de "problema em desenvolvimento" → padrão de sinais: febre, cardio, respiratório, letargia, misto) com 7 % de ruído de rótulo. Isso é declarado abertamente: o objetivo é demonstrar a *arquitetura* e o *processo de avaliação*, não acurácia clínica real. Métricas em `ai_core/trained/metrics.json` e `notebooks/avaliacao_modelo.ipynb`.
- **Por que não LLM aqui:** dado tabular, precisa de saída calibrada, reproduzível e avaliável. Um LLM seria caro, lento, não determinístico e não auditável.

### C3 — Motor de regras (`protocols.yaml`)
- Protocolo vacinal por espécie, vermifugação (90 d), check-up por faixa etária (filhote/adulto/sênior), adesão mínima (80 %), retorno pós-procedimento, limites absolutos de segurança (herdados do Sprint 2) e desvio sustentado do baseline (≥ 2,5 σ).
- **Regra sempre vence o modelo**: se a antirrábica venceu, a ação entra na fila independentemente do score. O modelo *prioriza*; não *revoga* protocolo.

### C4 — Recomendação de serviços (content-based)
- Tags de contexto do pet (espécie, faixa etária, nível de risco, fatores, categorias das ações, engajamento do tutor) × tags de cada serviço em `services.yaml`; similaridade de cosseno + boost para serviços que atendem ações de prioridade 1–2.
- Resultado: "próximo serviço" com justificativa legível (*por quê*) e preço em BRL.

### IoB — Internet of Behaviors (`iob.py`)
- O sensor aqui está no **humano**: `message_sent → opened → replied → appointment_booked / no_show / med_confirmed`.
- Deriva engajamento (alto/médio/baixo), canal e horário efetivos, taxa de faltas e de confirmação de doses.
- Alimenta o tom da mensagem, a estratégia de contato ("confirmar 24 h antes", "oferecer teleconsulta") e o recomendador (tutor de baixo engajamento → serviço de menor atrito).

### C5 — IA Generativa (Claude API) + RAG leve
- Recebe o contexto **estruturado e já decidido** (score, fatores, ações, serviços, perfil do tutor) + 3 trechos recuperados por TF-IDF de `knowledge/*.md` (protocolos da clínica, sinais de alerta, serviços, diretrizes de comunicação).
- Devolve JSON com `tutor_message` (WhatsApp, ≤ 550 caracteres, linguagem simples, uma ação) e `vet_briefing` (técnico, com z-scores). *Structured output* garante o formato.
- Modelo `claude-opus-5`, esforço `low` (tarefa de redação curta), prompt de sistema estável com cache.
- **Fallback determinístico**: sem credenciais, com erro de API, ou recusa do modelo, um template gera os dois textos a partir do mesmo contexto. Mesmo padrão do YOLO no Sprint 2 (sem pesos → modo simulado). A demo nunca depende de rede.

### Guardrails
- Bloqueia: posologia numérica, prescrição, diagnóstico afirmado, doença afirmada como fato, medicamento nomeado, instrução de administração.
- Exige: nome do pet e chamada para ação (agendar/consulta/clínica).
- Aplica o aviso: *"Não substitui avaliação do médico-veterinário"* (responsabilidade técnica — CFMV).
- Saída bloqueada → template. O bloqueio fica registrado no insight.

## Personalização — quatro eixos

| Eixo | Fonte | Efeito |
|---|---|---|
| Clínico | espécie, raça, idade, comorbidades, prontuário | protocolo aplicável, prioridade das ações |
| Comportamental do pet | telemetria + visão (baseline individual) | o que é "anormal" para *este* pet |
| Relacional (IoB) | eventos do tutor | canal, horário, tom, atrito da ação sugerida |
| Econômico | catálogo da clínica | serviço compatível com risco e perfil |

## Limitações conhecidas

- Dados de treino sintéticos: o modelo aprende a estrutura que definimos, não fisiologia real. Em produção, os rótulos viriam de desfechos reais (consulta em 7 dias, diagnóstico registrado).
- O baseline individual precisa de ~15 leituras estáveis; o dashboard mostra explicitamente quando ainda está usando prior.
- O RAG é TF-IDF sobre uma base pequena; escala para embeddings + pgvector quando a base de conhecimento crescer.
- Sem hardware físico nesta sprint: ESP32 e MPU6050 são simulados (Wokwi + Python).
