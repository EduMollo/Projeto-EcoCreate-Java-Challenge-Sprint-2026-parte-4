package br.com.fiap.clyvovet.service;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
@Service @Profile("postgres")
public class RelatorioPostgresService implements RelatorioBancoService {
 private final JdbcTemplate jdbc;
 public RelatorioPostgresService(JdbcTemplate jdbc) { this.jdbc=jdbc; }
 public String exportar(Long clinica) {
  return jdbc.queryForObject("select clyvovet.exportar_atendimentos(cast(? as bigint))",String.class,clinica);
 }
 public String faturamento(int ano) {
  return jdbc.queryForObject("select clyvovet.faturamento_anual(?)",String.class,ano);
 }
}
