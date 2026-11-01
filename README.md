# ClyvoVet — Java Advanced, Sprint 4

Continuação da aplicação aprovada com 100/100 na Sprint 3. Conecta tutores, pets e clínicas: cadastro, agenda, atendimento, retorno automático, vacinação, medicamentos, histórico e dashboard de saúde por regras explicáveis.

## Stack e arquitetura
Java 17+, Spring Boot 4.1.0, Spring MVC, Thymeleaf, Bootstrap 5.3, Spring Security, JPA, Flyway, H2 para demonstração e Oracle 19c+ para integração com Database e PostgreSQL para publicação com Supabase. Playwright para QA.

Controllers → services → repositories → banco. As views Thymeleaf recebem formulários validados. ADMIN administra usuários/clínicas/relatórios; VETERINARIO conduz atendimento; TUTOR consulta somente seus pets. A autorização por posse continua nos serviços.

## Evolução
- Tema claro/escuro no layout compartilhado, com preferência persistida.
- Perfis Oracle e PostgreSQL separados, migrations próprias e administrador inicial por variável de ambiente.
- Relatórios administrativos chamando procedures empacotadas via JDBC.
- Exportação JSON de atendimentos, faturamento por ano e auditoria de pets.
- Seis cenários de navegador e novos testes de acesso aos relatórios.
- Publicação na Vercel com Docker, Supabase e sessões compartilhadas no PostgreSQL. DevOps da equipe usa .NET e não é implementado neste repositório.

## Executar a demonstração local

Requer JDK 17+ e internet para baixar dependências e estilos Bootstrap.

~~~powershell
.\mvnw.cmd spring-boot:run
~~~

Abrir http://localhost:8080. Nesse modo H2 em memória, os dados são reiniciados ao encerrar a aplicação.

| Perfil | Usuário de demonstração | Senha |
|---|---|---|
| ADMIN | admin | admin123 |
| VETERINARIO | vet | vet123 |
| TUTOR | tutor | tutor123 |

Essas contas pertencem somente à carga H2. O perfil Oracle inicia sem elas e sem dados de demonstração. O console H2 local está em /h2-console, JDBC jdbc:h2:mem:clyvovetdb, usuário sa e senha vazia; no Oracle ele está desabilitado.

## Testes e build

~~~powershell
.\mvnw.cmd clean verify
.\mvnw.cmd clean package
java -jar target/clyvovet-web-4.0.0.jar
~~~

Se a máquina restringir escrita no cache Maven padrão, executar .\verificar.ps1, que usa o diretório temporário.

QA requer a aplicação H2 local em execução e os dados iniciais. Em outro terminal:

~~~powershell
cd qa
npm install
npx playwright install chromium
npm run headed
npm run report
~~~

Detalhes e limites: [QA](docs/QA.md). Registrar e versionar o package-lock.json gerado pela instalação antes da entrega. O teste de jornada cria uma consulta fictícia; reiniciar H2 para restaurar a carga original.

## Oracle e Database

Seguir [INTEGRACAO-ORACLE.md](docs/INTEGRACAO-ORACLE.md). Utilizar schema novo, não o banco da entrega anterior. O Flyway usa db/oracle e cria as tabelas TB_*; as views CLINICA/TUTOR/PET/ATENDIMENTO leem as mesmas linhas. Essa é uma evolução do modelo anterior e precisa estar refletida na modelagem da entrega Database.

Em /relatorios, ADMIN pode baixar JSON e consultar faturamento. As chamadas são PKG_CLYVOVET.PRC_ATENDIMENTOS_JSON e PKG_CLYVOVET.PRC_FATURAMENTO_CLINICA, com OUT CLOB. O trigger TRG_AUD_PET delega auditoria ao package. As rotinas foram adaptadas para o novo modelo, não copiadas integralmente da Sprint 3.

Os scripts de verificação e índices MongoDB estão em sql/. Não existe dataset Oracle exportado nesta entrega local, pois o banco externo ainda não foi conectado. O score de sensores não foi integrado ao dashboard de regras Java.

## Publicação

A aplicação está publicada em **https://clyvovet.vercel.app**. O frontend Thymeleaf e o backend Spring Boot executam juntos no Container Runtime da Vercel, com banco Supabase PostgreSQL e HTTPS. A branch de produção é `main`.

Na Vercel, usar `SPRING_PROFILES_ACTIVE=postgres,vercel`, `PORT=8080` e `COOKIE_SECURE=true`. O perfil `vercel` habilita Spring Session JDBC: as sessões ficam nas tabelas privadas criadas pela migration V6, com cookies HttpOnly, Secure e SameSite=Lax. Configurar `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` protegida; o administrador existente é reutilizado. `ADMIN_PASSWORD` só é necessária para inicializar uma base sem administrador. Guia: [DEPLOY-VERCEL.md](docs/DEPLOY-VERCEL.md).

Supabase é PostgreSQL: o perfil postgres já está preparado com funções alternativas de relatórios e auditoria, sem executar PL/SQL. Ver [SUPABASE.md](docs/SUPABASE.md) para criar o projeto e configurar Session pooler/JDBC. As tabelas ficam no schema privado clyvovet; a autenticação continua no Spring Security. Essa publicação não substitui a integração com a base Oracle exigida por Database.

## Validação

- Build completo via clean verify concluído com BUILD SUCCESS; JAR executável clyvovet-web-4.0.0.jar gerado.
- Build Docker na Vercel em 01/11/2026: 33 testes identificados, 29 executados com sucesso, zero falhas/erros e 4 ignorados. Inclui o teste de serialização do usuário autenticado. Os ignorados são os testes opcionais OracleIntegracaoTest e PostgresIntegracaoTest, dependentes de configuração externa.
- Quatro testes Node do seletor de tema aprovados.
- A aplicação foi iniciada pelo usuário com Supabase e o acesso à interface foi confirmado. A migration 5 teve um timeout ao alterar a tabela interna do Flyway; o SQL foi corrigido para restringir RLS e permissões às tabelas da aplicação.
- Prévia visual do painel conferida nos dois temas e em largura de celular; login desktop também revisado.
- Publicação de produção concluída; página de login HTTPS conferida no navegador. Logs confirmam conexão PostgreSQL 17.11, schema `clyvovet` na versão 6 e inicialização do servidor. A consulta aos logs após a abertura não encontrou erros de runtime.
- Ainda pendentes: login autenticado e CRUD na aplicação publicada, execução dos seis cenários Playwright, teste automatizado PostgreSQL, evidências de persistência/relatórios no banco real e gravação dos vídeos. O build padrão usa H2 para os testes de integração e não comprova esses itens externos.

## Documentação e entrega

- [Publicação com Supabase](docs/SUPABASE.md)
- [Publicação na Vercel](docs/DEPLOY-VERCEL.md)
- [Decisões e justificativas](docs/DECISOES-SPRINT4.md)
- [Plano QA e evidências](docs/QA.md)
- [Roteiro de apresentação até 15 minutos](docs/ROTEIRO-SPRINT4.md)
- [README anterior e descrição detalhada dos fluxos](docs/README-SPRINT3.md)
- Diagramas da Sprint 3: docs/der.drawio e docs/diagrama-classes.drawio (históricos, ainda não atualizados com os objetos Oracle da Sprint 4).

Antes da entrega: executar todas as verificações pendentes e inserir o link real do vídeo abaixo. Não entregar estes campos em aberto.

| Entregável | Situação |
|---|---|
| Novo repositório GitHub | https://github.com/EduMollo/Projeto-EcoCreate-Java-Challenge-Sprint-2026-parte-4 |
| Aplicação online | https://clyvovet.vercel.app |
| Vídeo Java (até 15 minutos) | Gravação e link pendentes |
| Evidências QA e Azure Boards | Execução, gravação e acesso do professor pendentes |

## Equipe — 2TDSPF

| Integrante | RM |
|---|---|
| Mathaus Victor Souza Marcelino | 564146 |
| Luan Peixoto Marins Rocha | 562258 |
| Eduardo Novaes Mollo | 561515 |
| Carlos Alberto Guedes Neto | 566022 |
| Vinicius Luis Exposito Morassi | 563340 |

A turma consta no projeto SQL; confirmar os dados finais dos integrantes antes da submissão. A divisão real das contribuições deve ser registrada pela equipe. Prazo informado: 04/11/2026.
