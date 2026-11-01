package br.com.fiap.clyvovet.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest @ActiveProfiles("oracle")
@EnabledIfEnvironmentVariable(named="RUN_ORACLE_TESTS",matches="true")
class OracleIntegracaoTest {
 @Autowired RelatorioOracleService relatorios;
 @Autowired JdbcTemplate jdbc;
 @Test void objetosCompiladosEProceduresExecutaveis() {
  assertThat(jdbc.queryForObject("select count(*) from user_errors where name like 'PKG_CLYVO%' or name in ('TRG_CV4_AUD_PET','TRG_CV4_CONSULTA_CAT')",Integer.class)).isZero();
  assertThat(relatorios.exportar(null)).startsWith("[").endsWith("]");
  assertThat(relatorios.faturamento(2026)).contains("Total:");
 }
 @Test void rejeitaClinicaInvalida() { assertThatThrownBy(()->relatorios.exportar(-1L)).isInstanceOf(org.springframework.dao.DataAccessException.class); }
}
