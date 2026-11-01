package br.com.fiap.clyvovet.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.MedicamentoRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VacinaRepository;
import br.com.fiap.clyvovet.service.risco.AvaliadorDeRisco;
import br.com.fiap.clyvovet.service.risco.ContextoSaudePet;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:democarga;DB_CLOSE_DELAY=-1")
@Transactional
class CargaDemonstracaoIntegracaoTest {
    @Autowired CargaDemonstracaoService carga;
    @Autowired JdbcTemplate jdbc;
    @Autowired PetRepository pets;
    @Autowired ConsultaRepository consultas;
    @Autowired VacinaRepository vacinas;
    @Autowired MedicamentoRepository medicamentos;
    @Autowired AvaliadorDeRisco avaliador;

    @Test void cargaTemRelacionamentosStatusDatasEValoresConsistentes() {
        long usuariosAntes = contar("select count(*) from tb_usuario");
        var resumo = carga.carregar();
        assertThat(resumo).isEqualTo(new CargaDemonstracaoService.Resumo(true, 6, 32, 60, 190, 120, 50, 310));
        assertThat(contar("select count(*) from tb_usuario")).isEqualTo(usuariosAntes);
        assertThat(contar("select count(distinct especie) from tb_pet where microchip like 'DEMO-S4-%'")).isEqualTo(6);
        assertThat(contar("select count(distinct tipo_consulta) from tb_consulta c join tb_pet p on p.id=c.id_pet where p.microchip like 'DEMO-S4-%'")).isEqualTo(7);
        assertThat(contar("select count(distinct status) from tb_consulta c join tb_pet p on p.id=c.id_pet where p.microchip like 'DEMO-S4-%'")).isEqualTo(4);
        assertThat(contar("select count(*) from tb_consulta c join tb_pet p on p.id=c.id_pet where p.microchip like 'DEMO-S4-%' and c.data_consulta < p.data_nascimento")).isZero();
        assertThat(contar("select count(*) from tb_consulta c join tb_pet p on p.id=c.id_pet where p.microchip like 'DEMO-S4-%' and c.status='CANCELADA' and c.motivo_cancelamento is null")).isZero();
        assertThat(contar("select count(*) from tb_consulta c join tb_pet p on p.id=c.id_pet where p.microchip like 'DEMO-S4-%' and c.data_retorno <= c.data_consulta")).isZero();
        assertThat(contar("select count(*) from tb_medicamento m join tb_consulta c on c.id=m.id_consulta where m.id_pet<>c.id_pet")).isZero();
    }

    @Test void cenariosGeramRiscoBaixoMedioAltoEMedicamentosAtivos() {
        carga.carregar();
        int[] esperados = {0, 100, 40, 50, 0};
        for (int i = 0; i < esperados.length; i++) {
            Long id = jdbc.queryForObject("select id from tb_pet where microchip=?", Long.class,
                    String.format("DEMO-S4-%03d", i + 1));
            var contexto = new ContextoSaudePet(pets.findById(id).orElseThrow(),
                    consultas.findByPetIdOrderByDataConsultaDescIdDesc(id),
                    vacinas.findByPetIdOrderByDataAplicacaoDesc(id),
                    medicamentos.findByPetIdOrderByDataInicioDesc(id), LocalDate.now());
            assertThat(avaliador.avaliar(contexto).getScore()).as("cenario %s", i + 1).isEqualTo(esperados[i]);
            if (i == 4) assertThat(contexto.medicamentosAtivos()).hasSize(2);
        }
    }

    @Test void repetirCargaNaoDuplicaNemDesfazAlteracoesFeitasPeloUsuario() {
        var primeira = carga.carregar();
        jdbc.update("update tb_pet set peso_kg=? where microchip='DEMO-S4-001'", new BigDecimal("11.75"));
        var segunda = carga.carregar();
        assertThat(segunda.criada()).isFalse();
        assertThat(segunda.consultas()).isEqualTo(primeira.consultas());
        assertThat(segunda.pets()).isEqualTo(primeira.pets());
        assertThat(jdbc.queryForObject("select peso_kg from tb_pet where microchip='DEMO-S4-001'", BigDecimal.class))
                .isEqualByComparingTo("11.75");
    }

    private long contar(String sql) { return jdbc.queryForObject(sql, Long.class); }
}
