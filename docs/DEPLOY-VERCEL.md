# Publicação Vercel

Projeto: https://vercel.com/tempests-projects-77349f79/clyvovet
Aplicação pública: https://clyvovet.vercel.app
Variáveis: https://vercel.com/tempests-projects-77349f79/clyvovet/settings/environment-variables
Repositório: EduMollo/Projeto-EcoCreate-Java-Challenge-Sprint-2026-parte-4; branch de produção main.
Runtime: Container, Dockerfile na raiz, porta 8080, região iad1 (mesma região do banco informado).

## Configuração
SPRING_PROFILES_ACTIVE=postgres,vercel
PORT=8080
COOKIE_SECURE=true
DB_URL=jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require
DB_USERNAME=postgres.zznnuwevoeuxaznplxhy

DB_PASSWORD deve ser preenchida pelo usuário diretamente na Vercel, como Secret/Sensitive, somente em Production. Não enviar no chat nem salvar no repositório. É a mesma senha de banco utilizada no iniciar-supabase.ps1.

O administrador já existente no banco é reutilizado. ADMIN_PASSWORD somente é exigida quando ainda não existe administrador; não é necessário trocar a senha do site para publicar a mesma base.

## Sessões
O perfil vercel habilita Spring Session JDBC com as tabelas privadas spring_session e spring_session_attributes, criadas pela nova migration V6. Isso permite compartilhar o login entre instâncias e reinícios do container. Cookies de sessão exigem HTTPS, são HttpOnly, SameSite=Lax e expiram após 30 minutos de inatividade. O perfil local postgres continua usando o login local de antes. A serialização do principal tem teste específico; a validação real entre instâncias depende da publicação e da conexão externa.

As variáveis de Production não são copiadas para Preview. Previews não devem acessar automaticamente a base de produção. Evitar publicar um Preview enquanto suas credenciais próprias não forem configuradas.

## Sequência
1. Salvar DB_PASSWORD na tela de variáveis, com alvo Production e tipo Secret/Sensitive.
2. Publicar o commit preparado na main.
3. Criar deployment de Production para esse commit.
4. Acompanhar build, conferir migrations e login HTTPS.
5. Validar CRUD e persistência usando dados fictícios, depois atualizar o README com o endereço público real.

Publicação de produção concluída em 01/11/2026 para o commit adbaed18174ae957ec51c7dc4253c743c7634893, com status READY. Build Docker aprovado: 33 testes identificados, 29 aprovados, 4 opcionais ignorados, zero falhas/erros. A página de login foi aberta sem autenticação Vercel no domínio público; a autenticação da aplicação continua obrigatória. Logs de inicialização confirmam conexão com Supabase PostgreSQL 17.11 e schema na versão 6. A inspeção após a abertura não encontrou erros de runtime. Login autenticado, CRUD e compartilhamento de sessão entre instâncias ainda precisam de validação no ambiente publicado.
