# Decisões da Sprint 4

1. Preservar as entregas anteriores e trabalhar nesta pasta separada. O feedback anterior foi 100/100.
2. Manter MVC/Thymeleaf, formulários, perfis, autorização por posse, atendimento e score Strategy.
3. Adicionar tema Bootstrap 5.3 por data-bs-theme, preferência local e alternativa quando o armazenamento está bloqueado. O tema alcança login e cadastro pelo layout compartilhado.
4. Separar migrations H2 (demonstração/testes existentes) e Oracle 19c+ (integração real). Nenhuma alteração do schema antigo é executada automaticamente.
5. O schema Oracle desta versão usa as tabelas TB_* do Java como modelo físico canônico. CLINICA, TUTOR, PET e ATENDIMENTO são views, não cópias. As views normalizam códigos para leitura pelas rotinas. O modelo físico de Database deverá refletir esta evolução caso a equipe a adote como entrega conjunta.
6. PKG_CLYVOVET evolui as finalidades das rotinas anteriores: exportar atendimentos e consultar faturamento. São novas implementações, com parâmetros OUT CLOB e JSON nativo Oracle 19c; não são uma cópia integral das rotinas da Sprint 3. Não se aplicou a restrição de JSON manual da sprint anterior às novas rotinas. Caso o professor exija preservá-la, essa parte precisa ser revista.
7. A auditoria usa trigger na tabela TB_PET, delegando ao package. Triggers permanecem objetos separados no Oracle; o código reutilizável fica no package. Auditoria e alteração participam da mesma transação.
8. O cálculo de risco de sensores não foi conectado nem substituiu o score Java. A aplicação continua sem coletor IoT.
9. Oracle inicia sem dados pessoais ou senhas públicas. O primeiro administrador é criado de forma transacional usando ADMIN_PASSWORD. Os exemplos de demonstração permanecem restritos ao H2.
10. Relatórios são exclusivos de ADMIN. O H2 informa indisponibilidade; não simula execução Oracle. Valores não são interpolados no SQL e detalhes de erros SQL não são enviados ao usuário.
11. QA: seis cenários de navegador em Playwright, além dos testes JUnit existentes e novos testes de autorização de relatórios. Os cenários de navegador usam a carga H2 e não devem ser executados contra produção.
12. DevOps é entregue com .NET. Nenhum pipeline Azure foi acrescentado ao Java.
13. Publicação Java continua pendente. Dockerfile prepara execução com Oracle. Vercel Container Runtime pode ser avaliado; Supabase é PostgreSQL e exigiria outro perfil e reimplementação das rotinas. Nenhum recurso externo foi provisionado.

## Fontes consultadas
- https://docs.spring.io/spring-boot/appendix/application-properties/
- https://vercel.com/docs/functions/runtimes/container
- https://supabase.com/docs/guides/database/overview

## Uso de IA e colaboração
Esta evolução foi implementada com assistência de IA. A equipe deve revisar, executar e conseguir explicar as decisões e o código. Os documentos da Sprint 3 são históricos; não comprovam contribuições novas. Registrar contribuições reais e revisões antes da entrega, sem atribuir trabalho não realizado.

## Atualização: escolha de Supabase
Após dificuldade de cadastro na Oracle Cloud, o usuário optou por Supabase. Foi acrescentado o perfil postgres com Flyway próprio, schema privado, pool de no máximo três conexões, funções SQL alternativas para relatórios e auditoria PL/pgSQL. RelatorioBancoService permite manter as implementações Oracle e PostgreSQL sem alterar os fluxos da interface. Docker passa a usar postgres por padrão. Spring Security continua responsável por login e posse. Nenhum projeto remoto foi alterado. Essa alternativa não executa os packages da entrega Oracle e não representa atendimento automático do requisito de base compartilhada de Database.

## Acabamento visual
Mantida a identidade azul-petroleo, com fundo claro suave e tema escuro azul-marinho. Componentes compartilham variaveis de cor e tipografia do sistema, sem novas dependencias de frontend. Painel recebe cabecalho de boas-vindas, indicadores com icones e estados vazios orientativos. Login usa duas colunas em desktop e formulario direto no celular. Links ativos sao destacados por script sem alterar rotas ou regras. Incluidos foco visivel, link para pular navegacao e respeito a movimento reduzido.

Verificacao: previa HTML temporaria montada com os mesmos templates e CSS, usando dados ilustrativos. Conferidos painel claro/escuro, painel a 390 px sem overflow horizontal e composicao de login desktop. A previa foi removida apos a verificacao; nao foram usados dados de usuarios nem alterados registros. Quatro testes de tema aprovados. A aplicacao deve ser reiniciada para invalidar o cache Thymeleaf. Os fluxos autenticados nao foram reexecutados nesta alteracao visual.
