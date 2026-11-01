package br.com.fiap.clyvovet.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class RelatorioControllerIntegracaoTest {
 @Autowired MockMvc mvc;
 @Test void tutorNaoAcessaExportacao() throws Exception {
  mvc.perform(get("/relatorios/atendimentos.json").with(user("tutor").authorities(()->"TUTOR"))).andExpect(redirectedUrl("/acesso-negado"));
 }
 @Test void veterinarioNaoAcessaRelatorios() throws Exception {
  mvc.perform(get("/relatorios").with(user("vet").authorities(()->"VETERINARIO"))).andExpect(redirectedUrl("/acesso-negado"));
 }
 @Test void adminVeIndisponibilidadeNoH2() throws Exception {
  mvc.perform(get("/relatorios").with(user("admin").authorities(()->"ADMIN"))).andExpect(status().isOk()).andExpect(model().attribute("relatoriosDisponiveis",false));
 }
 @Test void naoSimulaExportacaoOracleNoH2() throws Exception {
  mvc.perform(get("/relatorios/atendimentos.json").with(user("admin").authorities(()->"ADMIN"))).andExpect(status().isServiceUnavailable());
 }
 @Test void anoInvalidoApresentaErro() throws Exception {
  mvc.perform(get("/relatorios/faturamento?ano=1999").with(user("admin").authorities(()->"ADMIN"))).andExpect(status().isOk()).andExpect(model().attributeExists("erro"));
 }
}
