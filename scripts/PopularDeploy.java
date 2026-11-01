import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.Properties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import br.com.fiap.clyvovet.demo.CargaDemonstracaoService;

/** Usa a carga ja testada do JAR e aponta exclusivamente para a base do deploy. */
public class PopularDeploy {
    private static final String URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USER = "postgres.zznnuwevoeuxaznplxhy";

    public static void main(String[] args) throws Exception {
        if (args.length!=2 || !(args[0].equals("check") || args[0].equals("seed")))
            throw new IllegalArgumentException("Uso: PopularDeploy check|seed ARQUIVO_RESULTADO");
        System.setProperty("user.timezone","America/Sao_Paulo");
        ((ch.qos.logback.classic.Logger)org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
            .setLevel(ch.qos.logback.classic.Level.WARN);
        String password=System.getenv("CLYVOVET_DEPLOY_DB_PASSWORD");
        if(password==null || password.isBlank()) throw new IllegalArgumentException("Informe a senha do banco Supabase localmente.");
        var props=new Properties();
        props.setProperty("user",USER);props.setProperty("password",password);
        props.setProperty("connectTimeout","15");props.setProperty("socketTimeout","120");
        props.setProperty("ApplicationName","ClyvoVet-carga-demonstracao-deploy");
        try(var connection=DriverManager.getConnection(URL,props)) {
            if(!"PostgreSQL".equals(connection.getMetaData().getDatabaseProductName()))
                throw new SQLException("Banco inesperado; carga cancelada.");
            connection.setAutoCommit(false);
            try {
                try(var statement=connection.createStatement()) {
                    statement.execute("SET LOCAL search_path TO clyvovet");
                    try(var rows=statement.executeQuery("SELECT to_regclass('clyvovet.tb_pet'),to_regclass('clyvovet.tb_clinica'),to_regclass('clyvovet.tb_consulta')")) {
                        rows.next();
                        for(int i=1;i<=3;i++) if(rows.getString(i)==null)
                            throw new SQLException("Schema esperado da aplicacao ausente; nenhuma estrutura sera criada.");
                    }
                }
                var jdbc=new JdbcTemplate(new SingleConnectionDataSource(connection,true));
                if(args[0].equals("check")) {
                    System.out.println("Conexao ao banco do deploy confirmada: zznnuwevoeuxaznplxhy / clyvovet.");
                    System.out.println("Pets atuais: "+jdbc.queryForObject("select count(*) from tb_pet",Long.class));
                    System.out.println("Pets DEMO atuais: "+jdbc.queryForObject("select count(*) from tb_pet where microchip like 'DEMO-S4-%'",Long.class));
                    connection.rollback();return;
                }
                System.out.println("Aplicando carga ficticia na base do deploy; aguarde a confirmacao da transacao.");
                var summary=new CargaDemonstracaoService(jdbc).carregar();
                if(summary.criada() && (summary.clinicas()!=6 || summary.tutores()!=32 || summary.pets()!=60 ||
                        summary.consultas()!=190 || summary.vacinas()!=120 || summary.medicamentos()!=50 || summary.eventos()!=310))
                    throw new SQLException("Quantidade inesperada na carga nova; transacao revertida.");
                long inconsistencias=jdbc.queryForObject("""
                    select count(*) from tb_consulta c join tb_pet p on p.id=c.id_pet
                    where p.microchip like 'DEMO-S4-%' and
                    (c.data_consulta<p.data_nascimento or (c.status='CANCELADA' and c.motivo_cancelamento is null)
                    or c.data_retorno<=c.data_consulta)
                    """,Long.class);
                if(inconsistencias!=0) throw new SQLException("Dados DEMO inconsistentes; transacao revertida.");
                connection.commit();
                System.out.println(summary.criada()?"CARGA APLICADA E CONFERIDA NO BANCO DO DEPLOY.":
                    "A carga ja existia no banco do deploy; registros preservados, sem duplicacao.");
                System.out.println(summary);
                String result=String.format(java.util.Locale.ROOT,"""
                    {
                      "destino": "https://clyvovet.vercel.app",
                      "projeto_supabase": "zznnuwevoeuxaznplxhy",
                      "schema": "clyvovet",
                      "verificado_em": "%s",
                      "criada": %s,
                      "clinicas": %d, "tutores": %d, "pets": %d,
                      "consultas": %d, "vacinas": %d, "medicamentos": %d, "eventos": %d
                    }
                    """,OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")),summary.criada(),summary.clinicas(),
                    summary.tutores(),summary.pets(),summary.consultas(),summary.vacinas(),summary.medicamentos(),summary.eventos());
                Path destination=Path.of(args[1]);Files.createDirectories(destination.toAbsolutePath().getParent());
                Files.writeString(destination,result,StandardCharsets.UTF_8);
                System.out.println("Atualize a pagina do site para consultar os dados [DEMO].");
            } catch(Exception error) { connection.rollback();throw error; }
        }
    }
}
