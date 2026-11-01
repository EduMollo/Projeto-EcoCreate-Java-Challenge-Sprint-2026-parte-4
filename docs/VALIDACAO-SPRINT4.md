# Validação vigente — 01/11/2026

| Parte | Resultado |
|---|---|
| Maven/JUnit | 47 testes identificados; 43 executados com sucesso; zero falhas/erros; 4 opcionais de bancos externos ignorados |
| Empacotamento | clean verify com BUILD SUCCESS; JAR executável clyvovet-web-4.0.0.jar gerado com o POM real |
| Tela Oracle | 4 testes MVC aprovados: formulário/auditoria, leitura válida, limites e score; contrato substitui o serviço de banco |
| Contrato JDBC Oracle | 3 testes aprovados: parâmetros, retorno e propagação de falha |
| Playwright | 6 cenários aprovados em Chromium e H2; duração 12,8 segundos |
| Tema | 4 testes Node aprovados |
| Vercel | Publicação Java com PostgreSQL, HTTPS e sessões compartilhadas |
| Dados demonstrativos | 60 pets, 190 consultas, 120 vacinas, 50 medicamentos e 310 eventos no deploy, conforme registro da carga |

O resumo e o hash do JAR estão em [VALIDACAO-TECNICA.json](VALIDACAO-TECNICA.json).
A evidência Playwright sanitizada está em [evidencias/playwright-junit.xml](evidencias/playwright-junit.xml).
Os relatórios completos locais ficam nas pastas de teste ignoradas pelo Git.

## Oracle FIAP e MongoDB reais

O roteiro concluído pelo usuário em 01/11/2026 às 16:19 registrou Oracle 19.3,
12 objetos finais VALID e USER_ERRORS vazio, testes transacionais com rollback,
JSON real de 34 atendimentos e importação/reimportação MongoDB sem duplicatas.
Os dois testes OracleIntegracaoTest executaram exportação e faturamento pelo serviço Java.

Essa evidência permanece em Database/docs/2TDSPF_2026_Validacao.json.
A suíte mais recente, com 47 testes, não ativou os quatro opcionais externos e não
substitui nem altera a evidência da execução anterior.

Uma nova conexão Oracle pelo ambiente do agente foi bloqueada pela permissão de rede.
Isso não foi apresentado como falha do banco nem como nova execução bem-sucedida.
O fluxo Oracle por telas deverá ser mostrado na gravação, com o acesso local já configurado.

## Publicação e limites

O usuário confirmou login na aplicação publicada e executou a carga demonstrativa.
Os testes de navegador acima usam H2, não comprovam toda a jornada PostgreSQL.
A página de login, recursos estáticos e logs do deploy foram inspecionados; os ajustes
de desempenho observaram inicialização de 10–15 segundos, antes 19–20 segundos.
O primeiro acesso após inatividade ainda pode aguardar a inicialização do container.

O novo fechamento inclui no GitHub os fontes Oracle antes restritos à pasta local.
Os registros iniciais de impedimentos foram preservados em historico/VALIDACAO-INICIAL.md.

## Apresentação a concluir pela equipe

Gravar o vídeo Java de até 15 minutos com participação dos integrantes, e o vídeo narrado
de Database mostrando o backend Oracle e o MongoDB. Preencher docs/VIDEO.md e o arquivo
correspondente da entrega Database. Conferir acesso aos links antes de submeter.
