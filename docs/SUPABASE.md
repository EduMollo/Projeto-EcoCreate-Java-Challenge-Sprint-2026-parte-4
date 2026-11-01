# Publicação com Supabase (PostgreSQL)

## Escolha
O perfil postgres usa Supabase apenas como banco SQL, via JDBC. Login e autorização continuam no Spring Security. Não instalar supabase-js, não usar Supabase Auth e não colocar senha do banco no navegador. O schema clyvovet é privado e não deve ser adicionado aos schemas expostos na Data API.

O perfil Oracle continua disponível. As funções PostgreSQL são implementações alternativas, não executam packages PL/SQL. Essa opção de publicação sozinha não comprova a integração exigida com a base Oracle da disciplina Database; essa entrega precisa continuar coberta por backend que chame suas rotinas ou por alinhamento do modelo com a disciplina.

## Criar o projeto
1. Acessar https://supabase.com/dashboard e New project.
2. Nome clyvovet, região São Paulo quando disponível, plano Free. Guardar a senha do banco no gerenciador de senhas.
3. Aguardar o banco ficar disponível. Não usar um projeto de outro aplicativo.
4. Em Connect, selecionar Session pooler, porta 5432, formato JDBC. Copiar host, usuário e nome do banco. Não escolher Transaction pooler 6543 para o Hibernate.

## Variáveis no computador ou na hospedagem

~~~text
SPRING_PROFILES_ACTIVE=postgres
DB_URL=jdbc:postgresql://HOST_DO_SESSION_POOLER:5432/postgres?sslmode=require
DB_USERNAME=USUARIO_EXATO_DO_PAINEL
DB_PASSWORD=SENHA_DO_BANCO
ADMIN_USERNAME=admin
ADMIN_PASSWORD=SENHA_INICIAL_FORTE_COM_12_OU_MAIS_CARACTERES
COOKIE_SECURE=true
PORT=8080
~~~

Usar o host e usuário reais mostrados pelo painel, não adivinhar o nome da região ou referência. A senha fica separada da URL, evitando percent-encoding no JDBC. A senha ADMIN_PASSWORD é o login do site; DB_PASSWORD é a credencial SQL, e elas devem ser diferentes. Para HTTP local, usar COOKIE_SECURE=false.

O Flyway cria o schema clyvovet e aplica db/postgres. Hikari e Hibernate também usam esse schema. O login do banco deve poder criar esse schema e os objetos. A configuração inicial pode usar o usuário proprietário fornecido pelo projeto; separar usuário de migrations e usuário de execução é uma melhoria posterior que exigirá grants e políticas apropriadas.

Não executar manualmente os arquivos V1–V5 no SQL Editor para depois iniciar Flyway: deixar a aplicação gerenciar o histórico. Não importar a carga H2 ou o script destrutivo Oracle. Não aplicar simultaneamente os perfis oracle e postgres.

## Execução local
No PowerShell, definir as variáveis acima usando $env:NOME='valor' e executar .\verificar.ps1. Depois executar .\mvnw.cmd spring-boot:run ou java -jar target/clyvovet-web-4.0.0.jar, mantendo as mesmas variáveis.

No primeiro início é criado o administrador definido por ADMIN_USERNAME e ADMIN_PASSWORD. O banco começa sem pets, clínicas e tutores; cadastrar dados fictícios pela interface. Não existem contas vet/tutor com senhas padrão no perfil postgres. Criar os usuários apropriados pelo administrador.

## Conferência
No SQL Editor, executar sql/verificar-postgres.sql depois de cadastrar um pet e concluir uma consulta. Conferir JSON exportado e relatório pelo menu Relatórios, logado como ADMIN.

As tabelas têm RLS habilitada sem políticas públicas. O proprietário da tabela usado no backend pode operar normalmente; a autorização por tutor é aplicada no Spring Security/serviços. Isso não representa políticas RLS por tutor, pois a aplicação não usa JWT Supabase nem auth.uid(). Não conceder acesso anon/authenticated ao schema privado.

Teste real opcional em PROJETO DE TESTE dedicado: definir RUN_POSTGRES_TESTS=true e executar .\mvnw.cmd -Dtest=PostgresIntegracaoTest test com as credenciais de teste. A transação do teste desfaz os registros e auditorias criados; Flyway e bootstrap acontecem antes dessa transação. Não executar contra produção.

## Hospedagem da aplicação
O Dockerfile agora seleciona postgres por padrão; ainda é possível substituir SPRING_PROFILES_ACTIVE=oracle. O Supabase não hospeda o JAR/Thymeleaf. Publicar o container em plataforma compatível, configurando as variáveis acima. Vercel Container Runtime permanece uma opção a validar, incluindo o comportamento de sessões com cold starts e múltiplas instâncias. Não há publicação executada nesta etapa.

## Status e fontes
Adaptação de código local preparada. Ainda não foi aplicado schema em Supabase nem executado teste SQL em banco real: os projetos encontrados na conexão estão inativos e não foram alterados. Os testes do tema foram repetidos e passaram. O build completo permanece sujeito à disponibilidade de Maven Central.

- https://supabase.com/docs/guides/getting-started/quickstarts/spring-boot
- https://supabase.com/docs/guides/database/connecting-to-postgres
- https://supabase.com/pricing

## Conexao do projeto informada pelo usuario
Projeto: zznnuwevoeuxaznplxhy. Session pooler: aws-0-us-east-1.pooler.supabase.com:5432. Usuario: postgres.zznnuwevoeuxaznplxhy. Nenhuma senha foi recebida ou salva.

Para iniciar localmente, executar iniciar-supabase.ps1 na pasta do projeto. O script pede a senha SQL e a senha inicial do administrador sem eco no terminal, configura o perfil postgres apenas durante a execucao e restaura as variaveis ao terminar. A senha do administrador deve ter pelo menos 12 caracteres. O primeiro inicio ainda precisa validar o download das dependencias Maven, as migrations e a conexao real.
