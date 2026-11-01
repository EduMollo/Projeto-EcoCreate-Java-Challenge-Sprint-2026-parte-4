# Cronograma — ClyvoVet Web · Java Advanced · 3ª Sprint

**Elaborado em:** 11/09/2026 · **Início:** 15/09/2026 · **Entrega prevista:** _(confirmar no portal)_

> Preencher a coluna **Responsável** com o nome de cada integrante. A divisão nominal de tarefas foi
> um ponto cobrado no feedback da sprint anterior.

## Frentes de trabalho

| Frente | Escopo | Responsável |
|---|---|---|
| **A — Dados** | Entidades JPA (Lombok seguro), migrations Flyway V1–V6, dados de demonstração | _(nome)_ |
| **B — Segurança** | `Usuario`/`Perfil`, `SecurityConfig`, `UserDetailsService`, login/acesso negado, autorização por posse | _(nome)_ |
| **C — Visão e fluxos** | `layout.html` e fragmentos, telas, jornada do atendimento, dashboard de risco | _(nome)_ |

## Fases

| # | Fase | Período | Entregas | Responsável | Status |
|---|---|---|---|---|---|
| 0 | Fundação | 15/09 – 19/09 | Repositório; `pom.xml` na stack da aula (Boot 4.1.0 / Java 17) validado; app sobe com Flyway | Todos | ✅ |
| 1 | Domínio + Flyway | 22/09 – 03/10 | 7 entidades migradas com Lombok corrigido; enums em arquivos próprios; `Usuario`/`Perfil`; V1–V5 | Frente A | ✅ |
| 2 | Segurança | 06/10 – 17/10 | Matriz de rotas; BCrypt; `UsuarioAutenticado`; `/acesso-negado`; `AcessoPetService` (posse) | Frente B | ✅ |
| 3 | Frontend base | 20/10 – 24/10 | Layout único; navbar por perfil; três painéis iniciais; listagens; páginas de erro | Frente C | ✅ |
| 4 | Fluxo 1 — Atendimento | 27/10 – 31/10 | Máquina de estados em `Consulta`; telas agendar/atender/cancelar; prescrições; migration V6 | Frente C + A | ✅ |
| 5 | Fluxo 2 — Dashboard | 03/11 – 07/11 | `RegraDeRisco` (Strategy) com 6 regras; `AvaliadorDeRisco`; tela do dashboard | Frente C | ✅ |
| 6 | Polimento | 10/11 – 14/11 | Checklist anti-penalidade; auditoria de links por perfil; testes automatizados | Todos | ⏳ |
| 7 | Entrega | 17/11 – 21/11 | README; diagramas (UML e DER); vídeo ≤ 10 min; submissão no portal | Todos | ⏳ |

## Checklist de encerramento (fase 6)

- [ ] Nenhum controller acessa `Repository` diretamente
- [ ] Nenhuma regra de negócio em controller ou template
- [ ] Um único `layout.html`; nenhum `<head>` duplicado
- [ ] Nomes por extenso; zero código comentado; zero template de exemplo
- [ ] Login testado nos 3 perfis; rotas proibidas redirecionam para acesso negado
- [ ] Tutor A não acessa pet do Tutor B trocando o `{id}` na URL
- [ ] Todos os formulários testados com dados inválidos (mensagem na tela, não stacktrace)
- [ ] Todos os links clicados em cada perfil
- [ ] `mvnw.cmd clean package` sem erros; `mvnw.cmd test` verde

## Roteiro do vídeo (≈ 9 min)

| Tempo | Conteúdo |
|---|---|
| 0:30 | O que é a ClyvoVet e o que a sprint 3 entrega |
| 1:00 | Login `admin` — painel, usuários, clínicas |
| 1:00 | Login `vet` — tentar `/usuarios` → acesso negado |
| 1:00 | Login `tutor` — só os próprios pets; trocar `{id}` na URL → bloqueado |
| 2:00 | Fluxo 1 — agendar → iniciar → registrar atendimento com retorno → retorno criado automaticamente; uma validação falhando |
| 1:30 | Fluxo 2 — dashboard de Thor (alto) × Luna (baixo), alertas, linha do tempo |
| 1:30 | Código — `SecurityConfig`, `db/migration`, `flyway_schema_history` no console H2, `RegraDeRisco` |
| 0:30 | Fechamento |
