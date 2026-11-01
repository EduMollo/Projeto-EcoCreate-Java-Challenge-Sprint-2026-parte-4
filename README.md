# ClyvoVet — Java Advanced, Sprint 4

Continuação da Sprint 3 aprovada com 100/100. Entrega em **04/11/2026**, turma **2TDSPF**.

**Aplicação:** https://clyvovet.vercel.app
**Fontes:** https://github.com/EduMollo/Projeto-EcoCreate-Java-Challenge-Sprint-2026-parte-4
**Vídeo:** inserir o link em [docs/VIDEO.md](docs/VIDEO.md) após a gravação.

## Aplicação e arquitetura

Java 17+, Spring Boot 4.1.0, MVC, Thymeleaf, Bootstrap, Spring Security, JPA e Flyway.
Controllers recebem formulários validados, serviços aplicam as regras e repositories persistem os dados.
ADMIN administra usuários, clínicas e relatórios; VETERINARIO conduz atendimentos; TUTOR acessa somente seus pets.
A autorização por posse é conferida nos serviços.

A aplicação oferece cadastro, agenda, atendimento, retorno automático, cancelamento com motivo, vacinação,
medicamentos, histórico e painel de risco por regras explicáveis. O tema claro/escuro compartilha o layout
e preserva a preferência entre páginas.

## Bancos e execução

| Perfil | Uso | Dados |
|---|---|---|
| Padrão H2 | Demonstração e testes locais | Em memória, reiniciados ao encerrar |
| oracle | Integração da disciplina Database | Oracle FIAP, tabelas TB_* e views CV4_* |
| postgres,vercel | Aplicação publicada | Supabase PostgreSQL e sessões JDBC compartilhadas |

Para a demonstração H2:

~~~powershell
.\mvnw.cmd spring-boot:run
~~~

Abrir http://localhost:8080. Contas **exclusivas do H2**:

| Perfil | Usuário | Senha de demonstração |
|---|---|---|
| ADMIN | admin | admin123 |
| VETERINARIO | vet | vet123 |
| TUTOR | tutor | tutor123 |

Essas senhas não são as do deploy ou do Oracle. Configurações privadas ficam fora do Git e dos pacotes.

O Oracle usa `src/main/resources/db/oracle-sprint4`. Os packages leem as mesmas linhas usadas pelo JPA,
sem cópias paralelas. A instalação manual FIAP está na entrega Database e deve iniciar a aplicação
com `ORACLE_FLYWAY_ENABLED=false`. Flyway é alternativa apenas em schema novo.
Seguir [INTEGRACAO-ORACLE.md](docs/INTEGRACAO-ORACLE.md).

- `/relatorios`: exportação JSON e faturamento por procedures com OUT CLOB.
- `/relatorios/oracle`: leitura de sensores via procedure, score por função e auditoria.
- Agendamento Oracle: profissional selecionado no catálogo com FKs validadas.

A publicação usa PostgreSQL e não substitui a demonstração Oracle exigida por Database.

## Testes

~~~powershell
.\mvnw.cmd clean verify
node --test qa/tema.test.cjs
~~~

O script `verificar.ps1` usa cache Maven temporário quando o cache padrão não é gravável.
Os testes Oracle e PostgreSQL são opcionais, ativados por variáveis de ambiente; um teste ignorado
não comprova execução no banco externo.

Para os seis cenários de navegador, iniciar H2 e, em outro terminal:

~~~powershell
cd qa
npm ci
npx playwright install chromium
npm test
npm run report
~~~

Usar somente H2 com a carga inicial. O cenário de atendimento cria uma consulta fictícia.
O lockfile fixa as versões. Instruções e resultados em [QA.md](docs/QA.md) e
[VALIDACAO-SPRINT4.md](docs/VALIDACAO-SPRINT4.md).

## Deploy e entrega

A branch `main` publica na Vercel; `develop` contém os mesmos fontes de entrega.
Produção: `SPRING_PROFILES_ACTIVE=postgres,vercel`, `PORT=8080`, `COOKIE_SECURE=true`,
além de DB_URL, DB_USERNAME e DB_PASSWORD protegidas. O administrador existente é preservado.
Guias: [DEPLOY-VERCEL.md](docs/DEPLOY-VERCEL.md), [SUPABASE.md](docs/SUPABASE.md).

O container tem ajustes de inicialização, sessão PostgreSQL com upsert e cache de recursos com hash.
A plataforma ainda pode encerrar instâncias ociosas. Resultados em [DESEMPENHO-VERCEL.md](docs/DESEMPENHO-VERCEL.md).

[Decisões](docs/DECISOES-SPRINT4.md) • [Roteiro Java, até 15 minutos](docs/ROTEIRO-SPRINT4.md) •
[Vídeo a preencher](docs/VIDEO.md). Todos os integrantes devem participar da apresentação.
A demonstração Oracle e a importação MongoDB têm roteiro próprio na entrega Database.

A pasta `docs/historico` preserva registros anteriores. Os diagramas draw.io e a decisão de IA
da Sprint 3 são históricos; não representam uma nova integração de IA executada nesta sprint.
A modelagem Oracle atual está nos PDFs e SQL da entrega Database.

## Equipe — 2TDSPF

| Integrante | RM |
|---|---|
| Mathaus Victor Souza Marcelino | 564146 |
| Luan Peixoto Marins Rocha | 562258 |
| Eduardo Novaes Mollo | 561515 |
| Carlos Alberto Guedes Neto | 566022 |
| Vinicius Luis Exposito Morassi | 563340 |

O desenvolvimento recebeu assistência de IA. A equipe deve revisar, compreender e explicar o código.
A autoria dos commits usa o perfil EduMollo; a participação real de cada integrante é apresentada pela equipe.
