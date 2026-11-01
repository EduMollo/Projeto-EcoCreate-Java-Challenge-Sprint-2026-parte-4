# QA — automação de navegador

## Pré-condições
Aplicação executando com perfil padrão H2 e carga inicial; Node.js e Chromium do Playwright instalados. Não rodar contra dados reais. Cada teste tem um novo contexto de navegador; execução com um worker. O teste de atendimento cria registro próprio pela tela e não depende de uma consulta preexistente.

## Casos
| ID | Ação | Resultado esperado |
|---|---|---|
| QA01 | Login tutor e consulta de pets | Thor/Luna visíveis; Rex ausente |
| QA02 | Login com senha incorreta | Falha exibida e rota autenticada continua protegida |
| QA03 | Tutor acessa /pets/3/dashboard | Acesso negado sem score do outro pet |
| QA04 | Veterinário agenda/inicia/conclui nova consulta | Diagnóstico salvo e ação iniciar indisponível |
| QA05 | Alternância de tema, navegação e reload | Preferência mantida; volta ao claro |
| QA06 | Tutor acessa relatórios | Acesso negado |

## Execução e evidências
1. Executar npm install e npx playwright install chromium na pasta qa; versionar o lockfile gerado.
2. Com H2 iniciado, executar npm run headed. BASE_URL pode mudar o endereço; manter uma base de testes com a carga correspondente.
3. Mostrar playwright.config.js, cenários e terminal na gravação narrada. Abrir npm run report para resultados. O HTML, XML JUnit, vídeos e traces ficam em qa/playwright-report e qa/test-results, ignorados no Git por poderem conter dados de tela.
4. Vincular casos, resultados e falhas reais aos itens de teste manuais no Azure Boards. A imagem recebida contém somente a Parte B; os requisitos completos da Parte A não foram fornecidos. Os seis casos não substituem automaticamente essa parte.
5. Disponibilizar os fontes na branch develop pública e garantir o acesso do professor à organização/projeto de QA. Não foi criado projeto externo nem enviado convite nesta execução.

## Verificação adicional do tema
node --test qa/tema.test.cjs executa sem dependências externas. Quatro casos aprovados. Esse teste não substitui a inspeção visual nem os cenários de navegador.

## Situação
Os testes Playwright foram escritos e tiveram sintaxe verificada, mas não foram executados: a execução específica de navegador ainda está pendente. O build Maven completo e seus 28 testes padrão foram posteriormente aprovados. Não existem vídeos ou capturas apresentados como resultados de execução.
