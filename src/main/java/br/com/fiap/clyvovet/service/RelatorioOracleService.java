package br.com.fiap.clyvovet.service;
import java.sql.Types;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Service;
@Service @Profile("oracle")
public class RelatorioOracleService implements RelatorioBancoService {
 private final JdbcTemplate jdbc;
 public RelatorioOracleService(JdbcTemplate jdbc) { this.jdbc=jdbc; }
 public String exportar(Long clinica) { return executar("PRC_ATENDIMENTOS_JSON", clinica, Types.NUMERIC); }
 public String faturamento(int ano) { return executar("PRC_FATURAMENTO_CLINICA", String.valueOf(ano), Types.VARCHAR); }
 private String executar(String rotina, Object argumento, int tipo) {
  // Nome da rotina definido exclusivamente no codigo; parametros vinculados por JDBC.
  return jdbc.execute((ConnectionCallback<String>) conexao -> {
   try (var chamada=conexao.prepareCall("{call PKG_CLYVOVET."+rotina+"(?,?)}")) {
    chamada.setObject(1,argumento,tipo); chamada.registerOutParameter(2,Types.CLOB); chamada.execute();
    var clob=chamada.getClob(2);
    if(clob==null) return "";
    try(var reader=clob.getCharacterStream()) { var result=new StringBuilder(); char[] buffer=new char[8192]; int n;
     while((n=reader.read(buffer))!=-1) result.append(buffer,0,n); return result.toString();
    } catch(java.io.IOException erro) { throw new java.sql.SQLException("Falha na leitura do relatorio",erro); }
    finally { clob.free(); }
   }
  });
 }
}
