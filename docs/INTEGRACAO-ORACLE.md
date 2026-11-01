# Integração Oracle

## Ambiente
Oracle 19c ou superior, schema NOVO dedicado à Sprint 4, Java 17+ e Maven Wrapper com acesso às dependências. O schema precisa criar tabelas, views, packages e triggers e ter quota de tablespace. Não executar o script destrutivo da Sprint 3 neste schema.

Configurar variáveis no terminal (substituir valores; não versionar credenciais):

~~~powershell
$env:SPRING_PROFILES_ACTIVE='oracle'
$env:DB_URL='jdbc:oracle:thin:@//HOST:1521/SERVICE_NAME'
$env:DB_USERNAME='USUARIO'
$env:DB_PASSWORD='SENHA'
$env:ADMIN_PASSWORD='SENHA_INICIAL_COM_12_OU_MAIS_CARACTERES'
# Somente para HTTP local, sem proxy HTTPS:
$env:COOKIE_SECURE='false'
.\mvnw.cmd spring-boot:run
~~~

Para publicação HTTPS, manter COOKIE_SECURE=true. O Oracle usa somente db/oracle e não executa a carga H2. Flyway cria schema, constraints, perfis, campo de cancelamento e package. ADMIN_USERNAME é opcional (padrão admin). ADMIN_PASSWORD é exigida apenas quando ainda não há administrador. Alterações de credencial posteriores precisam de procedimento administrativo próprio.

## Demonstração de integração real
1. Entrar como administrador e cadastrar clínica, tutor e pet pela interface. Conferir via SELECT nas tabelas TB_CLINICA, TB_TUTOR e TB_PET.
2. Agendar, iniciar e concluir consulta. Conferir TB_CONSULTA e a view ATENDIMENTO: o mesmo registro aparece como CONCLUIDA e REALIZADO respectivamente.
3. Abrir Relatórios e baixar atendimentos.json. A aplicação chama PKG_CLYVOVET.PRC_ATENDIMENTOS_JSON via JDBC com OUT CLOB. Conferir o pet/tutor e o valor.
4. Consultar faturamento pelo ano da consulta. A aplicação chama PKG_CLYVOVET.PRC_FATURAMENTO_CLINICA. Somente consultas concluídas entram no total, calculado por cursor, com subtotal por clínica.
5. Alterar nome ou peso do pet. Conferir AUD_PET e seus valores anteriores/novos. Desativar um pet gera UPDATE, pois o Java usa desativação lógica; não apresentar isso como DELETE físico.
6. Rodar sql/verificar-oracle.sql para verificar compilação e consistência das views.

Não executar mudanças diretas no banco de produção para gerar evidências. Os dados usados devem ser fictícios e a base de teste isolada.

## Teste automatizado optativo
Em schema de teste dedicado, configurar as variáveis acima e RUN_ORACLE_TESTS=true, depois executar mvnw.cmd -Dtest=OracleIntegracaoTest test. Esse teste consulta USER_ERRORS, executa as duas procedures e verifica rejeição de clínica inválida. Não foi executado neste ambiente.

## MongoDB
O JSON exportado contém documentos de atendimento com pet e tutor aninhados. Para importar em uma base de demonstração (não contém evidência gerada enquanto Oracle não estiver conectado):

~~~text
mongoimport --uri "$MONGODB_URI" --collection atendimentos --file atendimentos.json --jsonArray
mongosh "$MONGODB_URI" --file sql/mongodb.js
~~~

No PowerShell, usar $env:MONGODB_URI no lugar de $MONGODB_URI. Não usar --drop sobre uma coleção existente. Para exportações repetidas usar uma coleção vazia ou política de atualização definida. O script de índices não apaga dados.
