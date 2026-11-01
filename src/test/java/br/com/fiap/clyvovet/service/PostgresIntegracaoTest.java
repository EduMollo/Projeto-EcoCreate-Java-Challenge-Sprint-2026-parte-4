package br.com.fiap.clyvovet.service;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest @ActiveProfiles("postgres")
@EnabledIfEnvironmentVariable(named="RUN_POSTGRES_TESTS",matches="true")
@Transactional
class PostgresIntegracaoTest {
 @Autowired RelatorioPostgresService relatorios;
 @Autowired JdbcTemplate jdbc;
 @Test void consultaExportadaFaturadaEAuditadaNaMesmaBase() {
  String tag=UUID.randomUUID().toString();
  String cpf=String.format("%011d",Math.floorMod(System.nanoTime(),100000000000L));
  Long tutor=jdbc.queryForObject("insert into clyvovet.tb_tutor(nome,cpf,email,telefone,data_nascimento) values(?,?,?,'11999999999',date '1990-01-01') returning id",Long.class,"Tutor QA",cpf,tag+"@example.test");
  Long clinica=jdbc.queryForObject("insert into clyvovet.tb_clinica(nome,cnpj,telefone,email,endereco,cidade,estado) values(?,?,'1130001000',?,'Rua QA','Sao Paulo','SP') returning id",Long.class,"Clinica QA",String.format("%014d",Math.floorMod(System.nanoTime(),100000000000000L)),tag+"@clinica.test");
  Long pet=jdbc.queryForObject("insert into clyvovet.tb_pet(nome,especie,raca,data_nascimento,peso_kg,id_tutor) values(?,'GATO','SRD',date '2022-01-01',4,?) returning id",Long.class,tag,tutor);
  jdbc.update("insert into clyvovet.tb_consulta(data_consulta,status,tipo_consulta,veterinario,valor,criado_em,id_pet,id_clinica) values(current_date,'CONCLUIDA','ROTINA','Veterinario QA',180,current_timestamp,?,?)",pet,clinica);
  assertThat(relatorios.exportar(clinica)).contains(tag).contains("180");
  assertThat(relatorios.faturamento(java.time.LocalDate.now().getYear())).contains("Clinica QA").contains("180.00");
  jdbc.update("update clyvovet.tb_pet set peso_kg=5 where id=?",pet);
  assertThat(jdbc.queryForObject("select count(*) from clyvovet.aud_pet where id_registro=?",Integer.class,pet)).isEqualTo(2);
  assertThat(jdbc.queryForObject("select valor_novo->>'peso_kg' from clyvovet.aud_pet where id_registro=? and tipo_operacao='UPDATE'",String.class,pet)).isEqualTo("5.00");
 }
 @Test void rejeitaParametrosInvalidos() {
  assertThatThrownBy(()->relatorios.exportar(-1L)).isInstanceOf(org.springframework.dao.DataAccessException.class);
 }
}
