# Decisões de implementação — ClyvoVet Web

Registro das escolhas técnicas e do porquê de cada uma. Serve de base para a avaliação oral:
cada item abaixo é uma pergunta provável, com a resposta que o código sustenta.

## 1. Spring Boot 4.1.0 com Java 17

Stack idêntica à do projeto de referência apresentado em aula (`gestao_faculdade`). O Boot 4 usa
artefatos modulares — `spring-boot-starter-webmvc`, `spring-boot-flyway`, `spring-boot-h2console`,
`spring-boot-starter-webmvc-test` — que não são os nomes do Boot 3. O build foi validado no primeiro
dia do projeto antes de qualquer modelagem, justamente para eliminar esse risco cedo.

## 2. Camada de serviço obrigatória (o exemplo da aula chamava o repository no controller)

Controllers só orquestram requisição → serviço → view. Regras de negócio, transações e
autorização por posse vivem em `service/`. Motivos: responsabilidade única (um controller gordo é a
penalidade mais cara da rubrica), testabilidade sem servlet e reaproveitamento — `PetService.buscar`
faz a checagem de posse uma vez e vale para ficha, dashboard, histórico e consultas.

## 3. Lombok nas entidades: o conjunto seguro, e por que `@Data` foi removido

Feedback da sprint anterior. `@Data` em entidade JPA gera `equals/hashCode` sobre todos os campos
(dispara carregamento de relações lazy e quebra a identidade dentro de coleções) e `toString`
recursivo em relações bidirecionais. Passamos a usar:

```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
```

com `@EqualsAndHashCode.Include` apenas no `id`, `@ToString.Include` só em escalares e
`@Builder.Default` em toda coleção inicializada.

**Limite conhecido dessa abordagem:** antes do `persist()` o `id` é nulo, logo duas entidades novas
são "iguais". Onde a entidade vive em um `Set` — `Perfil` dentro de `Usuario.perfis` — escrevemos
`equals/hashCode` à mão sobre a chave natural (`descricao`), que nunca é nula.

## 4. Enums em arquivos próprios

`StatusConsulta`, `Especie`, etc. eram classes internas das entidades. Foram extraídos para
`model/enums/` e ganharam `descricao` (texto para a tela) e, quando útil, a cor Bootstrap. A tela não
faz `if` de status; pergunta ao enum.

## 5. Máquina de estados dentro da entidade `Consulta`

`iniciarAtendimento()`, `concluir(...)` e `cancelar(...)` validam a transição e lançam
`RegraDeNegocioException` quando inválida. A entidade protege o próprio invariante; o
`ConsultaService` cuida dos efeitos colaterais (linha do tempo, agendamento automático do retorno).

## 6. `hasAuthority` em vez de `hasRole`

As authorities são gravadas exatamente como o enum (`ADMIN`, `VETERINARIO`, `TUTOR`), sem prefixo
`ROLE_`. `hasRole("ADMIN")` procuraria `ROLE_ADMIN` e negaria tudo — bug clássico. Seguimos o
padrão do exemplo da aula e mantivemos um único estilo em toda a configuração.

## 7. Autorização por posse na camada de serviço

Perfil libera a rota; posse libera o dado. `AcessoPetService.garantirAcesso(pet)` compara o tutor
do pet com o tutor do usuário logado e lança `AccessDeniedException`, que o Spring Security converte
em redirecionamento para `/acesso-negado`. Fica no serviço (não no controller) para valer em
qualquer caminho que chegue ao pet.

## 8. `UsuarioAutenticado` como principal

Em vez do `User` genérico do Spring, o principal carrega `id`, `nomeExibicao`, `idTutor` e perfis.
Isso evita consultar o banco a cada requisição para montar a navbar ou checar posse. Um
`@ControllerAdvice` publica `usuarioLogado` para todas as views — um único ponto de leitura do
`SecurityContextHolder`.

## 9. Score de risco como Strategy de verdade

Na sprint anterior o "Strategy" era uma cadeia de `if/else` em um método. Agora cada regra é uma
classe `@Component` que implementa `RegraDeRisco`; o Spring injeta `List<RegraDeRisco>` no
`AvaliadorDeRisco`. Regra nova = classe nova, sem editar nada existente (Open/Closed). O contexto
(`ContextoSaudePet`) é um `record` imutável, o que deixa as regras puras e testáveis sem banco.

## 10. Formulários separados das entidades + validação de unicidade declarativa

Os formulários (`controller/form/*Form`) carregam as anotações de Bean Validation; as entidades
carregam só o mapeamento. Unicidade (CPF, CNPJ, microchip, username) é uma constraint de classe
(`validation/@TutorUnico` etc.) cujo validador recebe o repository por injeção — o erro aparece no
campo certo via `th:errors`, sem `try/catch` repetido em cada controller.

## 11. `open-in-view=false` e `@EntityGraph`

Desligamos o *Open Session in View* (o Boot alerta sobre ele por padrão). Cada consulta usada por
uma tela declara via `@EntityGraph` as associações que a tela precisa. Evita N+1 e
`LazyInitializationException` de forma explícita, e obriga a pensar no que cada view consome.

## 12. Flyway com migration de evolução

`ddl-auto=none`; V1–V5 constroem schema e dados; **V6 altera uma tabela existente**
(`motivo_cancelamento`). Sem uma alteração incremental, um lote único de DDL não demonstra
"controle de versão". Colunas de enum são `VARCHAR` (o exemplo da aula usava o tipo `enum` do H2,
não portável). Os dados de demonstração usam datas relativas a `CURRENT_DATE` para que os alertas
de risco façam sentido em qualquer dia de apresentação.

## 13. Auto-cadastro restrito ao perfil Tutor

A tela de login oferece "Criar conta", mas `/cadastro` só produz contas `TUTOR`: o perfil é fixado
no `CadastroTutorService`, nunca vem do formulário. Deixar o usuário escolher o perfil num
cadastro público seria entregar `ADMIN` a qualquer visitante. O formulário compõe `TutorForm` +
`ContaForm` (`@Valid` em cascata), reaproveitando validações e unicidade em vez de duplicá-las;
`UsuarioForm` (cadastro pelo admin) estende `ContaForm`, então a confirmação de senha vale nos dois.

## 14. O que ficou fora, de propósito

- **API REST das sprints 1–2**: não foi portada. Um endpoint REST aberto seria um furo de segurança;
  protegido, seria escopo sem pontuação nesta sprint.
- **i18n pt/en** do exemplo da aula: não pontua na rubrica; o esforço foi para os itens avaliados.
- **Oracle/PostgreSQL**: H2 em memória zera o setup do avaliador. As migrations foram escritas para
  serem portáveis (exceto `DATEADD` nos dados de demonstração).

---

## Uso de inteligência artificial no processo

> **A preencher pela equipe, com honestidade — é um item explícito da avaliação oral.**
> Sugestão de estrutura:
>
> - **Onde a IA ajudou:** planejamento inicial a partir do briefing e do feedback; geração de
>   esqueleto de código e templates; revisão de padrões (Lombok em JPA, Strategy).
> - **O que foi revisado e alterado por nós:** _(exemplos concretos — arquivos, decisões)_
> - **O que foi rejeitado e por quê:** _(exemplos)_
> - **Regra adotada:** nada entra no repositório sem que alguém da equipe consiga explicar linha a linha.
