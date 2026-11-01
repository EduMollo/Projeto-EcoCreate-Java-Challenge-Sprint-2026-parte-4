# Ajustes de desempenho na Vercel

O deploy registrava inicializacoes do Spring Boot entre 19 e 20 segundos e erros de chave duplicada ao salvar SPRING_SECURITY_CONTEXT em spring_session_attributes durante requisicoes concorrentes.

- SessaoVercelConfig registra PostgreSqlJdbcIndexedSessionRepositoryCustomizer, do Spring Session 4.1. A insercao de atributos passa a usar ON CONFLICT ... DO UPDATE, mantendo os bytes do atributo e o login compartilhado entre instancias.
- O perfil vercel configura cache de um dia para recursos estaticos e URLs com hash do conteudo para CSS, JavaScript e imagens. HTML autenticado e formularios continuam com a politica de cache de seguranca do Spring Security.
- O container usa -XX:TieredStopAtLevel=1 para priorizar a inicializacao da JVM em instancias de curta duracao. O ajuste reduz o nivel maximo de compilacao JIT e pode limitar o desempenho maximo em cargas longas; a melhora deve ser medida nos logs do novo deploy.
- Mantido o pool de tres conexoes por instancia. Aumenta-lo sem medir poderia exceder o limite global do banco ao escalar containers.

O scale-to-zero da plataforma continua existindo; esses ajustes nao tornam o processo Java permanentemente ativo.

Validacao: teste da configuracao real do repositorio de sessoes, testes HTTP de cache/URLs versionadas, suite Java, build do deploy e verificacao posterior dos logs. A execucao real e os tempos observados devem acompanhar o resultado da publicacao.

Referencias oficiais:
- https://docs.spring.io/spring-session/reference/configuration/jdbc.html
- https://vercel.com/docs/functions/runtimes/container
- https://spring.io/blog/2018/12/12/how-fast-is-spring/
