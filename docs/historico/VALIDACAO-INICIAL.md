# Registro de validação

## Executado
- Tema: 4 testes Node aprovados (preferência, fallback do sistema, alternância e armazenamento bloqueado).
- Domínio: 9 testes existentes de ConsultaTest/AvaliadorDeRiscoTest aprovados usando JDK 21, javac e launcher JUnit com dependências em cache. Não foi necessário mudar a stack do projeto.
- Novas classes RelatorioOracleService, RelatorioController e AdministradorOracleConfig: compilação isolada aprovada com bibliotecas locais.
- Sintaxe JS: tema, configuração Playwright, cenários QA e script MongoDB aprovada.
- git status das duas entregas originais: sem alterações.

## Bloqueios reais
- Maven Wrapper tentou gravar fora do workspace; verificar.ps1 configura cache temporário gravável.
- Maven com cache temporário falhou por DNS indisponível para repo.maven.apache.org. O cache antigo só possui Boot 3.2.x, incompatível com a base Boot 4.1.0. A compilação integral contra esse cache antigo não constitui uma validação da stack e não foi considerada aprovada.
- Docker CLI encontrado; daemon desligado.
- Oracle externo não fornecido/conectado. A compilação de package e trigger no Oracle, leitura real de CLOB, Flyway Oracle, persistência e auditoria ainda precisam ser verificados.
- Playwright não foi executado nem a UI inspecionada em navegador, pois o build completo não iniciou.

## Próximas verificações
1. Em ambiente com acesso ao Maven Central, executar verificar.ps1 ou mvnw.cmd clean verify.
2. Rodar os seis cenários QA contra H2 e inspecionar contraste e navegação desktop/mobile.
3. Conectar schema Oracle 19c+ novo; iniciar perfil oracle e executar os testes optativos e sql/verificar-oracle.sql.
4. Demonstrar as chamadas às duas procedures pela interface com SELECT de evidência.
5. Revisar e atualizar a modelagem Database para o schema integrado antes de entregar a disciplina.
6. Criar o repositório remoto e publicar a aplicação na hospedagem definida. Não há deploy ou dataset exportado real nesta execução.

## Adaptação PostgreSQL/Supabase
- Interface RelatorioBancoService, adaptadores Oracle/PostgreSQL, controller e AdministradorBancoConfig compilaram isoladamente com o cache local.
- Os quatro testes do tema foram repetidos e passaram.
- Nenhuma migration foi aplicada a projeto Supabase. Os projetos encontrados estavam inativos e não foram modificados. O teste real PostgresIntegracaoTest e o script sql/verificar-postgres.sql foram preparados para a validação após a conexão.
- Corrigida a sintaxe Oracle ADD COLUMN para ADD na migration de cancelamento, ainda não aplicada.

## Correcao da migration 5 apos primeira conexao real
O log fornecido pelo usuario confirmou conexao PostgreSQL 17.11, aplicacao de V1-V4 e rollback completo de V5. O erro ocorreu ao habilitar RLS em flyway_schema_history, tabela interna utilizada pelo Flyway durante a migration. V5 foi corrigida para alterar apenas as onze tabelas de negocio/auditoria, incluindo as revogacoes de privilegios. Nao se alterou nenhuma migration ja aplicada com sucesso nesse projeto. Revisao estatica confirmou a exclusao do historico de todos os comandos SQL. A nova execucao SQL depende de reiniciar pelo iniciar-supabase.ps1 no computador do usuario. A tentativa local de testes Maven offline permaneceu bloqueada pela ausencia de Boot 4.1.0 no cache disponivel para o agente.

## Atualização após clean verify do usuário
O build completo terminou com BUILD SUCCESS e gerou o JAR executável. Relatórios Surefire conferidos: 32 testes encontrados; 28 passaram; nenhum erro/falha; quatro ignorados (dois Oracle, dois PostgreSQL). Esta execução supera o bloqueio de build registrado anteriormente. A conexão real e a interface com Supabase foram confirmadas pelo usuário, mas os testes automatizados opcionais de banco externo e a suíte Playwright ainda não foram executados.
