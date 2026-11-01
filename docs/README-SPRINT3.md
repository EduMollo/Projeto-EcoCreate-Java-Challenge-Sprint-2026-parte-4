# ClyvoVet Web — Jornada contínua de saúde do pet

> **FIAP · Java Advanced · 3ª Sprint (2026)** — aplicação web completa em Spring Boot com camada de
> visualização (Thymeleaf), controle de versão do banco (Flyway) e autenticação/autorização por perfil
> (Spring Security).

A **CLYVO VET** conecta tutores, pets e clínicas veterinárias em uma jornada contínua de cuidado.
Nas sprints 1 e 2 entregamos a API REST do domínio; nesta sprint o produto ganha **interface web**,
**três perfis de acesso** e **dois fluxos de negócio completos**: a jornada do atendimento e o
dashboard de saúde com score de risco.

---

## Sumário

1. [Tecnologias](#tecnologias)
2. [Pré-requisitos](#pré-requisitos)
3. [Como executar](#como-executar)
4. [Acesso à aplicação e credenciais](#acesso-à-aplicação-e-credenciais)
5. [Perfis e proteção de rotas](#perfis-e-proteção-de-rotas)
6. [Fluxos completos](#fluxos-completos)
7. [Flyway — versionamento do banco](#flyway--versionamento-do-banco)
8. [Arquitetura](#arquitetura)
9. [Testes](#testes)
10. [Documentação complementar](#documentação-complementar)
11. [Equipe](#equipe)

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem |
| Spring Boot | 4.1.0 | Base da aplicação (`spring-boot-starter-webmvc`) |
| Thymeleaf + Bootstrap 5.3 | — | Camada de visualização (server-side) |
| Spring Data JPA / Hibernate | — | Persistência |
| Flyway | — | Migrations versionadas do schema e dos dados |
| Spring Security | — | Autenticação (form login + BCrypt) e autorização por perfil |
| Bean Validation | — | Validação dos formulários |
| H2 (em memória) | — | Banco de dados de desenvolvimento e demonstração |
| Lombok | — | Redução de boilerplate, com o conjunto seguro para entidades JPA |
| JUnit 5 + MockMvc | — | Testes unitários e de integração |

## Pré-requisitos

- **JDK 17 ou superior** instalado (`java -version`).
- **Não é necessário instalar o Maven** — o projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`),
  que baixa a versão correta automaticamente.
- Acesso à internet na primeira execução (download das dependências e do Bootstrap via CDN).

## Como executar

```bash
git clone <url-do-repositorio>
cd clyvovet-web
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
./mvnw spring-boot:run
```

Aguarde a mensagem `Started ClyvoVetWebApplication` no console. Na subida, o Flyway cria o schema
e popula os dados de demonstração automaticamente — não há nenhum passo manual de banco.

Para gerar o pacote executável:

```bash
mvnw.cmd clean package
java -jar target/clyvovet-web-3.0.0.jar
```

## Acesso à aplicação e credenciais

| Recurso | Endereço |
|---|---|
| Aplicação | <http://localhost:8080> |
| Console H2 | <http://localhost:8080/h2-console> |

**Console H2:** JDBC URL `jdbc:h2:mem:clyvovetdb` · usuário `sa` · senha em branco.
Na tabela `flyway_schema_history` é possível conferir as seis migrations aplicadas.

Usuários criados pela migration `V5__inserir_usuarios.sql`:

| Perfil | Usuário | Senha | Quem é |
|---|---|---|---|
| **ADMIN** | `admin` | `admin123` | Administração da rede ClyvoVet |
| **VETERINARIO** | `vet` | `vet123` | Dra. Camila Rocha |
| **TUTOR** | `tutor` | `tutor123` | Mariana Souza — tutora de *Thor* e *Luna* |

## Perfis e proteção de rotas

A autorização é feita em duas camadas:

1. **Por perfil (rota)** — no `SecurityFilterChain` (`config/SecurityConfig.java`):

| Rota | ADMIN | VETERINARIO | TUTOR |
|---|:---:|:---:|:---:|
| `/usuarios/**`, `/clinicas/**` | ✅ | ❌ | ❌ |
| `/pets/**`, `/tutores/**`, `/consultas/**` (gestão) | ✅ | ✅ | ❌ |
| `/pets/{id}`, `/pets/{id}/dashboard`, `/pets/{id}/historico` (leitura) | ✅ | ✅ | ✅* |
| `/meus-pets` | ❌ | ❌ | ✅ |
| `/login`, `/cadastro`, `/css/**`, `/h2-console/**` | público | público | público |

**Criar conta na tela de login (`/cadastro`):** o auto-cadastro é público, mas cria **somente**
contas de perfil `TUTOR` — o formulário registra o tutor (nome, CPF, e-mail, telefone, nascimento)
e a conta de acesso numa única transação. Contas `ADMIN` e `VETERINARIO` só são criadas por um
administrador em `/usuarios/novo`. Validações: CPF/e-mail/usuário únicos, senha com confirmação.

2. **Por posse (dado)** — `service/AcessoPetService.java`: um tutor só acessa pets vinculados ao
   próprio cadastro. Trocar o `{id}` na URL de outro tutor resulta em **Acesso negado**, mesmo com a
   rota liberada para o perfil. (*) É isso que o asterisco da tabela indica.

Tentativas de acesso a rotas proibidas redirecionam para `/acesso-negado`. O menu só exibe os
itens permitidos ao perfil logado.

## Fluxos completos

### 1. Jornada do atendimento (máquina de estados da consulta)

```
AGENDADA ──► EM_ANDAMENTO ──► CONCLUÍDA ──► (retorno agendado automaticamente)
    │              │
    └──────────────┴──► CANCELADA (exige motivo)
```

1. **Agendar** (`/consultas/nova`) — pet, clínica, data (não pode ser passada), tipo, veterinário.
2. **Iniciar atendimento** — só a partir de *Agendada*.
3. **Registrar atendimento** — diagnóstico obrigatório, prescrição, valor e **data de retorno**
   (deve ser posterior à consulta). Ao concluir, o sistema:
   - grava o evento na **linha do tempo** do pet;
   - se houver data de retorno, **agenda automaticamente** uma nova consulta do tipo *Retorno*.
4. **Prescrever medicamento / registrar vacina** — a partir de uma consulta em andamento ou
   concluída; cada registro alimenta a carteira de vacinação, a lista de medicamentos e a linha do tempo.
5. **Cancelar** — apenas consultas abertas, com motivo obrigatório (coluna adicionada pela migration `V6`).

As transições são métodos da entidade `Consulta`; uma transição inválida lança
`RegraDeNegocioException`, que vira mensagem de erro na tela.

**Roteiro de demonstração:** entre como `vet`, abra a consulta de *Nina* agendada para hoje, clique
em *Iniciar atendimento*, depois em *Registrar atendimento*, informe um diagnóstico e uma data de
retorno e conclua. Veja o retorno criado em *Consultas* e o evento em *Linha do tempo*.

### 2. Dashboard de saúde com score de risco e alertas proativos

`/pets/{id}/dashboard` consolida o histórico do pet e calcula um **score de risco (0–100)** a
partir de regras independentes (padrão **Strategy** — `service/risco/`):

| Regra | Quando dispara | Pontos |
|---|---|---|
| `SemConsultaRegistradaRegra` | Nunca teve consulta concluída | 40 |
| `ConsultaAtrasadaRegra` | Última consulta há > 180 / 365 / 540 dias | 10 / 25 / 35 |
| `VacinaVencidaRegra` | Vacinas com validade expirada | 10 por vacina (máx. 30) |
| `DoseAtrasadaRegra` | Próxima dose já vencida | 10 por dose (máx. 20) |
| `ConsultaNaoRealizadaRegra` | Consulta agendada com data passada | 15 |
| `PetSeniorRegra` | Pet com 7+ anos (10+ pesa mais) | 10 / 15 |

Classificação: **Baixo** (< 40), **Médio** (40–69), **Alto** (≥ 70). Adicionar uma regra nova é
criar uma classe que implementa `RegraDeRisco` — nada mais precisa mudar.

**Roteiro de demonstração:** compare o dashboard de *Thor* (risco alto: vacina vencida, retorno não
realizado, sênior) com o de *Luna* (risco baixo). Entre como `tutor` e veja que só esses dois pets
aparecem em *Meus pets*; tente abrir `/pets/3/dashboard` e observe o acesso negado.

## Flyway — versionamento do banco

`spring.jpa.hibernate.ddl-auto=none`: o Hibernate **não** cria nem altera tabelas. Todo o schema
vem das migrations em `src/main/resources/db/migration`:

| Migration | Conteúdo |
|---|---|
| `V1__criar_tabelas.sql` | Tabelas do domínio, de usuários e de perfis |
| `V2__criar_constraints.sql` | Chaves estrangeiras, uniques e índices |
| `V3__inserir_perfis.sql` | Dados de referência: `ADMIN`, `VETERINARIO`, `TUTOR` |
| `V4__popular_dados_demo.sql` | Tutores, clínicas, pets, consultas, vacinas, medicamentos e eventos (datas relativas a `CURRENT_DATE`) |
| `V5__inserir_usuarios.sql` | Usuários de acesso com senha em BCrypt |
| `V6__adicionar_motivo_cancelamento.sql` | **Evolução do schema:** nova coluna exigida pela regra de cancelamento |

## Arquitetura

```
br.com.fiap.clyvovet
├── config/          SecurityConfig, UsuarioLogadoControllerAdvice
├── controller/      controllers MVC (um por agregado) + form/ (objetos de formulário validados)
├── exception/       exceções de domínio + TratadorDeExcecoes (@ControllerAdvice)
├── model/           entidades JPA + enums/
├── repository/      Spring Data JPA (com @EntityGraph para as telas)
├── security/        UsuarioAutenticado, UserDetailsService, UsuarioLogadoProvider
├── service/         regras de negócio · mapper/ (form ⇄ entidade) · risco/ (Strategy)
└── validation/      constraints de unicidade (CPF, CNPJ, microchip, username)
```

Princípios seguidos: controller fino → service → repository; transições de estado na entidade;
um único `layout.html` com fragmentos; validação declarativa nos formulários (Bean Validation,
inclusive unicidade); autorização por posse na camada de serviço. As decisões e seus porquês estão em
[`docs/DECISOES.md`](docs/DECISOES.md).

## Testes

```bash
mvnw.cmd test
```

- `ConsultaTest` — máquina de estados da consulta.
- `AvaliadorDeRiscoTest` — estratégias de risco, ordenação e teto do score.
- `SegurancaIntegracaoTest` — sobe o contexto (Flyway + H2 + Security), confere as 6 migrations e
  testa a proteção de rotas com login real dos três perfis, incluindo a checagem de posse do tutor.
- `ConsultaServiceIntegracaoTest` — jornada do atendimento sobre os dados semeados: retorno
  agendado automaticamente, eventos na linha do tempo, cancelamento com motivo.
- `CadastroControllerIntegracaoTest` — auto-cadastro pela tela de login: rota pública, senhas
  divergentes, usuário/CPF duplicados e login da conta recém-criada com perfil `TUTOR`.

## Documentação complementar

| Documento | Caminho |
|---|---|
| Diagrama de classes (UML) | `docs/diagrama-classes.drawio` (abrir em [diagrams.net](https://app.diagrams.net)) |
| Diagrama entidade-relacionamento (DER) | `docs/der.drawio` |
| Cronograma e divisão de tarefas | `docs/CRONOGRAMA.md` |
| Decisões de implementação e uso de IA | `docs/DECISOES.md` |
| Vídeo de demonstração | _(link a preencher na entrega)_ |

## Equipe

| Integrante | RM |
|---|---|
|Mathaus Victor Souza Marcelino| RM: 564146 |
|Luan Peixoto Marins Rocha| RM: 562258 |
|Eduardo Novaes Mollo| RM: 561515 |
|Carlos Alberto Guedes Neto| RM: 566022 |
|Vinicius Luis Exposito Morassi| RM: 563340 |
