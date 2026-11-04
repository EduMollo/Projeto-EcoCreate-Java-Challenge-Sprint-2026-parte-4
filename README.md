# ClyvoVet — Java Advanced, Sprint 4

**Aplicação:** https://clyvovet.vercel.app
**Fontes:** [**Github**](https://github.com/EduMollo/Projeto-EcoCreate-Java-Challenge-Sprint-2026-parte-4)
[**Video**](https://youtu.be/RujXMfOUj8U)

**Acesso a Plataforma**

**Login:** professor

**Senha:** 123456

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

## Equipe — 2TDSPF

| Integrante | RM |
|---|---|
| Mathaus Victor Souza Marcelino | 564146 |
| Luan Peixoto Marins Rocha | 562258 |
| Eduardo Novaes Mollo | 561515 |
| Carlos Alberto Guedes Neto | 566022 |
| Vinicius Luis Exposito Morassi | 563340 |

