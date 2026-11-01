package br.com.fiap.clyvovet.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import br.com.fiap.clyvovet.controller.form.LeituraSensorForm;
import br.com.fiap.clyvovet.service.OracleClinicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedCaseInsensitiveMap;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Renderiza as telas reais; o contrato de banco e substituido, sem alegar execucao PL/SQL. */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:oracle-web-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.flyway.locations=classpath:db/migration",
    "spring.flyway.table=flyway_schema_history"
})
@ActiveProfiles("oracle")
@AutoConfigureMockMvc
class OracleClinicoControllerIntegracaoTest {
    @Autowired MockMvc mvc;
    @MockitoBean OracleClinicoService clinico;

    @BeforeEach void catalogos() {
        // JdbcTemplate devolve mapas com chaves Oracle em maiusculas, insensiveis a caixa.
        Map<String,Object> pet = new LinkedCaseInsensitiveMap<>();
        pet.put("ID", 1L); pet.put("NOME", "Rex"); pet.put("ESPECIE", "CAO");
        Map<String,Object> auditoria = new LinkedCaseInsensitiveMap<>();
        auditoria.put("ID_AUDITORIA", 1L); auditoria.put("ID_REGISTRO", 1L);
        auditoria.put("TIPO_OPERACAO", "INSERT"); auditoria.put("NOME_USUARIO", "DEMO");
        auditoria.put("DATA_HORA", "2026-11-01 12:00:00");
        when(clinico.pets()).thenReturn(List.of(pet));
        when(clinico.auditoria()).thenReturn(List.of(auditoria));
    }

    @Test void adminRenderizaFormularioEAuditoriaComChavesOracle() throws Exception {
        mvc.perform(get("/relatorios/oracle").with(user("admin").authorities(() -> "ADMIN")))
            .andExpect(status().isOk()).andExpect(view().name("relatorio/sensores"))
            .andExpect(content().string(containsString("Rex")))
            .andExpect(content().string(containsString("INSERT")));
    }

    @Test void registraLeituraValidadaERedireciona() throws Exception {
        when(clinico.registrarLeitura(any(LeituraSensorForm.class))).thenReturn(1000L);
        mvc.perform(post("/relatorios/oracle/leituras").with(csrf())
            .with(user("admin").authorities(() -> "ADMIN"))
            .param("petId", "1").param("dispositivo", "ESP32-DEMO")
            .param("temperatura", "38.5").param("frequenciaCardiaca", "90")
            .param("spo2", "98").param("atividade", "60"))
            .andExpect(redirectedUrl("/relatorios/oracle"))
            .andExpect(flash().attribute("mensagemSucesso", "Leitura 1000 registrada com sucesso."));
        verify(clinico).registrarLeitura(any(LeituraSensorForm.class));
    }

    @Test void leituraForaDosLimitesNaoChamaProcedure() throws Exception {
        mvc.perform(post("/relatorios/oracle/leituras").with(csrf())
            .with(user("admin").authorities(() -> "ADMIN"))
            .param("petId", "1").param("dispositivo", "ESP32-DEMO")
            .param("temperatura", "38.5").param("frequenciaCardiaca", "90")
            .param("spo2", "101").param("atividade", "60"))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("leituraSensorForm", "spo2"));
        verify(clinico, never()).registrarLeitura(any());
    }

    @Test void apresentaRetornoDaFuncaoNaTela() throws Exception {
        when(clinico.calcularRisco(1L)).thenReturn(new BigDecimal("42.5"));
        mvc.perform(get("/relatorios/oracle/risco?petId=1")
            .with(user("admin").authorities(() -> "ADMIN")))
            .andExpect(status().isOk()).andExpect(model().attribute("scoreSensor", new BigDecimal("42.5")))
            .andExpect(content().string(containsString("42.5")));
    }
}
