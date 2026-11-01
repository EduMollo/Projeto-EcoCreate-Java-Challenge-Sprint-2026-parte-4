package br.com.fiap.clyvovet.service;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.LeituraSensorForm;

/** Integra as rotinas clinicas empacotadas ao mesmo DataSource usado pelo JPA. */
@Service
@Profile("oracle")
public class OracleClinicoService {
    private final JdbcTemplate jdbc;

    public OracleClinicoService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> pets() {
        return jdbc.queryForList("select id, nome, especie from tb_pet where ativo=1 order by nome");
    }

    @Transactional(readOnly = true)
    public List<String> veterinarios() {
        return jdbc.queryForList("select nome from CV4_VETERINARIO order by nome", String.class);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularRisco(Long petId) {
        return jdbc.execute((ConnectionCallback<BigDecimal>) connection -> {
            try (var call = connection.prepareCall("{? = call PKG_CLYVOVET.FN_CALC_RISCO_PET(?)}")) {
                call.registerOutParameter(1, Types.NUMERIC);
                call.setLong(2, petId);
                call.execute();
                return call.getBigDecimal(1);
            }
        });
    }

    @Transactional
    public long registrarLeitura(LeituraSensorForm form) {
        return jdbc.execute((ConnectionCallback<Long>) connection -> {
            try (var call = connection.prepareCall("{call PKG_CLYVOVET.PRC_REGISTRAR_LEITURA(?,?,?,?,?,?,?)}")) {
                call.setLong(1, form.getPetId());
                call.setString(2, form.getDispositivo().trim());
                call.setBigDecimal(3, form.getTemperatura());
                call.setInt(4, form.getFrequenciaCardiaca());
                call.setBigDecimal(5, form.getSpo2());
                call.setBigDecimal(6, form.getAtividade());
                call.registerOutParameter(7, Types.NUMERIC);
                call.execute();
                return call.getLong(7);
            }
        });
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> auditoria() {
        return jdbc.queryForList("""
            select id_auditoria, id_registro, tipo_operacao, nome_usuario, data_hora
            from CV4_AUD_PET order by id_auditoria desc fetch first 30 rows only
            """);
    }
}
