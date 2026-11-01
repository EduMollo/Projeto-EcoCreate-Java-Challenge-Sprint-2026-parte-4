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
- Dockerfile para futura publicação. DevOps da equipe usa .NET e não é implementado neste repositório.

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

A disciplina exige acesso online. A publicação real ainda está pendente. O Dockerfile executa agora com SPRING_PROFILES_ACTIVE=postgres. Para Oracle, substituir essa variável por oracle. Configurar DB_URL, DB_USERNAME, DB_PASSWORD, ADMIN_PASSWORD e PORT=8080 na plataforma escolhida; usar HTTPS e COOKIE_SECURE=true.

Vercel oferece Container Runtime: https://vercel.com/docs/functions/runtimes/container . Avaliar a sessão HTTP no ambiente com múltiplas instâncias: as sessões atuais ficam em memória e podem exigir armazenamento compartilhado antes de publicar nesse cenário.

Supabase é PostgreSQL: o perfil postgres já está preparado com funções alternativas de relatórios e auditoria, sem executar PL/SQL. Ver [SUPABASE.md](docs/SUPABASE.md) para criar o projeto e configurar Session pooler/JDBC. As tabelas ficam no schema privado clyvovet; a autenticação continua no Spring Security. Essa publicação não substitui a integração com a base Oracle exigida por Database.

## Validação

- Build completo via clean verify concluído com BUILD SUCCESS; JAR executável clyvovet-web-4.0.0.jar gerado.
- Relatórios Surefire: 32 testes identificados, 28 executados com sucesso, zero falhas/erros e 4 ignorados. Os ignorados são os testes opcionais OracleIntegracaoTest e PostgresIntegracaoTest, dependentes de configuração externa.
- Quatro testes Node do seletor de tema aprovados.
- A aplicação foi iniciada pelo usuário com Supabase e o acesso à interface foi confirmado. A migration 5 teve um timeout ao alterar a tabela interna do Flyway; o SQL foi corrigido para restringir RLS e permissões às tabelas da aplicação.
- Prévia visual do painel conferida nos dois temas e em largura de celular; login desktop também revisado.
- Ainda pendentes: execução dos seis cenários Playwright, teste automatizado PostgreSQL, evidências de persistência/relatórios no banco real, publicação e gravação dos vídeos. O build padrão usa H2 para os testes de integração e não comprova esses itens externos.

## Documentação e entrega

- [Publicação com Supabase](docs/SUPABASE.md)
- [Decisões e justificativas](docs/DECISOES-SPRINT4.md)
- [Plano QA e evidências](docs/QA.md)
- [Roteiro de apresentação até 15 minutos](docs/ROTEIRO-SPRINT4.md)
- [README anterior e descrição detalhada dos fluxos](docs/README-SPRINT3.md)
- Diagramas da Sprint 3: docs/der.drawio e docs/diagrama-classes.drawio (históricos, ainda não atualizados com os objetos Oracle da Sprint 4).

Antes da entrega: executar todas as verificações pendentes, inserir o endereço online e o link real do vídeo abaixo. Não entregar estes campos em aberto.

| Entregável | Situação |
|---|---|
| Novo repositório GitHub | https://github.com/EduMollo/Projeto-EcoCreate-Java-Challenge-Sprint-2026-parte-4 |
| Aplicação online | Publicação pendente |
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
