package br.com.fiap.clyvovet.config;
import java.time.LocalDate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration @Profile({"oracle", "postgres"})
public class AdministradorBancoConfig {
 @Bean ApplicationRunner criarAdministrador(JdbcTemplate jdbc, PasswordEncoder encoder, Environment env) {
  return args -> {
   var total=jdbc.queryForObject("select count(*) from tb_usuario_perfil up join tb_perfil p on p.id=up.id_perfil where p.descricao='ADMIN'",Integer.class);
   if(total!=null && total>0) return;
   String senha=env.getRequiredProperty("ADMIN_PASSWORD");
   if(senha.length()<12) throw new IllegalStateException("ADMIN_PASSWORD deve ter pelo menos 12 caracteres");
   var transacao=new org.springframework.transaction.support.TransactionTemplate(new org.springframework.jdbc.datasource.DataSourceTransactionManager(jdbc.getDataSource()));
   transacao.executeWithoutResult(status -> {
    jdbc.update("insert into tb_usuario(username,senha,nome_exibicao,ativo,data_criacao) values(?,?,?,?,?)",env.getProperty("ADMIN_USERNAME","admin"),encoder.encode(senha),"Administrador",true,LocalDate.now());
    jdbc.update("insert into tb_usuario_perfil(id_usuario,id_perfil) select u.id,p.id from tb_usuario u cross join tb_perfil p where u.username=? and p.descricao='ADMIN'",env.getProperty("ADMIN_USERNAME","admin"));
   });
  };
 }
}
