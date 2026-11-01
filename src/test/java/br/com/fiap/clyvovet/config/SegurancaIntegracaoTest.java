package br.com.fiap.clyvovet.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Sobe o contexto completo (Flyway + H2 + Security) e exercita a proteção de rotas
 * com logins reais dos usuários semeados pelas migrations.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SegurancaIntegracaoTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Flyway flyway;

	@Test
	void flywayAplicaTodasAsMigrations() {
		assertThat(flyway.info().applied()).hasSize(6);
		assertThat(flyway.info().pending()).isEmpty();
	}

	@Test
	void anonimoEhRedirecionadoParaLogin() throws Exception {
		mockMvc.perform(get("/pets"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void adminAcessaGestaoDeUsuarios() throws Exception {
		MockHttpSession sessao = autenticar("admin", "admin123");

		mockMvc.perform(get("/usuarios").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/clinicas").session(sessao)).andExpect(status().isOk());
	}

	@Test
	void veterinarioNaoAcessaRotasExclusivasDoAdmin() throws Exception {
		MockHttpSession sessao = autenticar("vet", "vet123");

		mockMvc.perform(get("/consultas").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/usuarios").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
	}

	@Test
	void tutorSoEnxergaOsPropriosPets() throws Exception {
		MockHttpSession sessao = autenticar("tutor", "tutor123");

		mockMvc.perform(get("/meus-pets").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/pets/1/dashboard").session(sessao)).andExpect(status().isOk());

		mockMvc.perform(get("/pets/3/dashboard").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
		mockMvc.perform(get("/pets").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
	}

	private MockHttpSession autenticar(String usuario, String senha) throws Exception {
		return (MockHttpSession) mockMvc.perform(formLogin("/login").user(usuario).password(senha))
				.andExpect(authenticated())
				.andReturn()
				.getRequest()
				.getSession(false);
	}

}
