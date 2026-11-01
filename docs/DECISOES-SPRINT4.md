# Decisões da Sprint 4 — 01/11/2026

1. **Continuidade:** preservar a Sprint 3 aprovada e evoluir uma cópia própria da Sprint 4.
2. **Arquitetura:** manter MVC/Thymeleaf e as camadas controller, serviço e repository. Isso reaproveita os fluxos aprovados e permite formulários com validação e autorização por posse.
3. **Bancos por perfil:** H2 para demonstração isolada; Oracle para Database; PostgreSQL/Supabase para publicação. Não misturar migrations de bancos diferentes.
4. **Oracle vigente:** usar db/oracle-sprint4 e CV4_FLYWAY_HISTORY. A instalação manual FIAP desativa Flyway. db/oracle permanece somente como histórico da primeira preparação.
5. **Base compartilhada:** JPA usa TB_* e packages consultam views CV4_* sobre as mesmas linhas. Veterinário, serviço e leitura de sensor são entidades próprias. Constraints e índices CV4 evitam colisões com a Sprint 3.
6. **PL/SQL:** cinco packages, incluindo fachada PKG_CLYVOVET, agrupam JSON manual, clínica, relatórios e auditoria. Triggers são objetos Oracle separados e delegam a lógica reutilizável aos packages. Auditoria acompanha a transação.
7. **Integração em tela:** relatórios usam CallableStatement e OUT CLOB. O monitoramento registra leituras por procedure e mostra o risco calculado pela função preservada da Sprint 3. Os parâmetros são vinculados; erros SQL internos não são enviados ao usuário.
8. **Dois scores:** o score Oracle por sensores complementa o painel Java por regras. Não há coletor IoT nem nova IA externa apresentados como funcionalidades executadas.
9. **Segurança:** perfis e posse controlados no backend; relatórios somente ADMIN. Oracle/PostgreSQL reaproveitam o administrador existente. ADMIN_PASSWORD inicializa apenas uma base sem administrador; contas H2 são exclusivamente demonstrativas.
10. **Publicação:** Vercel Container Runtime com PostgreSQL em schema privado clyvovet. Sessões JDBC compartilham login entre instâncias. Upsert oficial do Spring Session trata atributos concorrentes.
11. **Desempenho:** recursos públicos com URLs de conteúdo e cache de um dia; HTML autenticado continua sem cache público. TieredStopAtLevel=1 prioriza inicialização, com possível redução de throughput em cargas longas. Pool mantido em três conexões por instância para respeitar limites do banco.
12. **Validação:** distinguir testes H2, contrato JDBC, renderização das telas e execução real Oracle/MongoDB. Resultado de mock não comprova PL/SQL. Os seis cenários Playwright usam H2 e nunca devem rodar contra produção.
13. **Entrega:** fonte Java no GitHub, aplicação publicada e vídeo Java de até 15 minutos. Database tem pacote próprio e vídeo com Oracle e MongoDB. DevOps .NET e Azure Boards/QA são entregas de suas disciplinas, não foram inventados como concluídos aqui.

## Histórico e participação

Registros iniciais de impedimentos foram preservados em docs/historico. A situação vigente está em
VALIDACAO-SPRINT4.md; textos históricos não substituem os resultados atuais.

O desenvolvimento recebeu assistência de IA. Contribuições dos integrantes devem refletir o trabalho real,
sem atribuições fictícias. A equipe precisa compreender e explicar as decisões e o código.
