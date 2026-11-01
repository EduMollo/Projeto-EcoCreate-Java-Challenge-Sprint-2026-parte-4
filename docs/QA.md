# QA — automação de navegador

## Resultado em 01/11/2026

**6 cenários aprovados, 0 falhas**, Chromium, Playwright 1.56.1, um worker, H2 isolado.
A execução durou 12,8 segundos. Não foram usados dados de produção.

| Caso | Verificação executada |
|---|---|
| QA01 | Tutor autenticado vê Thor/Luna e não vê Rex |
| QA02 | Senha incorreta impede acesso autenticado |
| QA03 | Trocar o ID não permite acessar o pet de outro tutor |
| QA04 | Veterinário agenda, inicia e conclui atendimento, com diagnóstico salvo |
| QA05 | Tema persiste entre páginas e recarregamento |
| QA06 | Tutor não acessa relatórios administrativos |

Evidência versionada: [evidencias/playwright-junit.xml](evidencias/playwright-junit.xml).
Resumo consolidado: [VALIDACAO-TECNICA.json](VALIDACAO-TECNICA.json).
Os quatro testes Node do tema também passaram.

## Reproduzir

Requer JDK 17+, Node.js e H2 com a carga inicial. Primeiro compilar o Java e iniciar a aplicação.
Em outro terminal:

~~~powershell
cd qa
npm ci
npx playwright install chromium
npm test
npm run report
~~~

BASE_URL permite mudar a porta, por exemplo:
`$env:BASE_URL='http://127.0.0.1:18080'`.

O cenário QA04 cria sua própria consulta. Reiniciar H2 restaura os dados.
Não executar esta suíte contra Oracle, PostgreSQL ou o site de produção.

O lockfile fixa as dependências. Os relatórios HTML, traces e vídeos automáticos ficam
em qa/playwright-report e qa/test-results, fora do Git. Os vídeos automáticos não têm
narração e não substituem a apresentação exigida pela faculdade.

## Escopo das evidências

A suíte valida interface e backend com H2. A tela Oracle tem quatro testes MVC adicionais
de renderização, validação, redirecionamento e score com serviço substituído por contrato.
Esses testes não são apresentados como execução real de PL/SQL.

A execução real Oracle/MongoDB está registrada na entrega Database. Durante a gravação,
usar o perfil Oracle para mostrar procedures no banco real.

Azure Boards, acesso do professor e eventuais requisitos específicos da disciplina QA
são externos a esta entrega Java/Database. Não há organização ou convite fictício informado
como concluído. Os registros iniciais estão preservados em historico/QA-INICIAL.md.
