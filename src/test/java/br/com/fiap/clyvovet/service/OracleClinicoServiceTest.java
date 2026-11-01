package br.com.fiap.clyvovet.service;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import br.com.fiap.clyvovet.controller.form.LeituraSensorForm;
import static org.assertj.core.api.Assertions.*;

/** Contrato JDBC com interfaces JDK; nao executa PL/SQL nem simula sua logica. */
class OracleClinicoServiceTest {
    static class JdbcContrato {
        String sql;
        final Map<Integer,Object> parametros = new HashMap<>();
        final Map<Integer,Integer> saidas = new HashMap<>();
        SQLException falha;
        boolean fechou;
        final CallableStatement statement = (CallableStatement) Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class<?>[]{CallableStatement.class}, (proxy, method, args) -> {
                String name = method.getName();
                if (name.startsWith("set") && args != null && args.length >= 2) { parametros.put((Integer)args[0],args[1]); return null; }
                if (name.equals("registerOutParameter")) { saidas.put((Integer)args[0],(Integer)args[1]); return null; }
                if (name.equals("execute")) { if(falha != null) throw falha; return false; }
                if (name.equals("getLong")) return 1000L;
                if (name.equals("getBigDecimal")) return new BigDecimal("42.5");
                if (name.equals("close")) { fechou = true; return null; }
                if (name.equals("toString")) return "Contrato JDBC de teste";
                return null;
            });
        final Connection connection = (Connection) Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                if (method.getName().equals("prepareCall")) { sql=(String)args[0]; return statement; }
                if (method.getName().equals("isClosed")) return false;
                if (method.getName().equals("toString")) return "Conexao de contrato";
                return null;
            });
        OracleClinicoService service() { return new OracleClinicoService(new JdbcTemplate(new SingleConnectionDataSource(connection,true))); }
    }

    @Test void enviaTodosOsValoresParaProcedureERetornaChaveGerada() {
        JdbcContrato jdbc = new JdbcContrato();
        LeituraSensorForm form = new LeituraSensorForm();
        form.setPetId(1L); form.setDispositivo(" ESP32-DEMO ");
        form.setTemperatura(new BigDecimal("38.5")); form.setFrequenciaCardiaca(90);
        form.setSpo2(new BigDecimal("98")); form.setAtividade(new BigDecimal("60"));
        assertThat(jdbc.service().registrarLeitura(form)).isEqualTo(1000L);
        assertThat(jdbc.sql).isEqualTo("{call PKG_CLYVOVET.PRC_REGISTRAR_LEITURA(?,?,?,?,?,?,?)}");
        assertThat(jdbc.parametros).containsExactlyInAnyOrderEntriesOf(Map.of(
            1,1L,2,"ESP32-DEMO",3,new BigDecimal("38.5"),4,90,5,new BigDecimal("98"),6,new BigDecimal("60")));
        assertThat(jdbc.saidas).containsEntry(7,Types.NUMERIC);
        assertThat(jdbc.fechou).isTrue();
    }

    @Test void leScoreDoRetornoDaFuncao() {
        JdbcContrato jdbc = new JdbcContrato();
        assertThat(jdbc.service().calcularRisco(3L)).isEqualByComparingTo("42.5");
        assertThat(jdbc.sql).isEqualTo("{? = call PKG_CLYVOVET.FN_CALC_RISCO_PET(?)}");
        assertThat(jdbc.saidas).containsEntry(1,Types.NUMERIC);
        assertThat(jdbc.parametros).containsEntry(2,3L);
    }

    @Test void propagaFalhaDoBancoSemInventarScore() {
        JdbcContrato jdbc = new JdbcContrato();
        jdbc.falha = new SQLException("Pet sem leituras","72000",20023);
        assertThatThrownBy(() -> jdbc.service().calcularRisco(10L)).isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThat(jdbc.fechou).isTrue();
    }
}
