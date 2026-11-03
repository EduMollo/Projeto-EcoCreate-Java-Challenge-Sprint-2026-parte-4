package br.com.fiap.clyvovet.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Sobe o contexto completo (Flyway + H2 + Security) e exercita a proteção de rotas
 * com logins reais dos usuários semeados pelas migrations.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SegurancaIntegracaoTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Flyway flyway;

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private PasswordEncoder encoder;

	@Test
	void flywayAplicaTodasAsMigrations() {
		assertThat(flyway.info().applied()).hasSize(7);
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
		MockHttpSession sessao = autenticar("admin");

		mockMvc.perform(get("/usuarios").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/clinicas").session(sessao)).andExpect(status().isOk());
	}

	@Test
	void veterinarioNaoAcessaRotasExclusivasDoAdmin() throws Exception {
		MockHttpSession sessao = autenticar("vet");

		mockMvc.perform(get("/consultas").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/usuarios").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
	}

	@Test
	void tutorSoEnxergaOsPropriosPets() throws Exception {
		MockHttpSession sessao = autenticar("tutor");

		mockMvc.perform(get("/meus-pets").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/pets/1/dashboard").session(sessao)).andExpect(status().isOk());

		mockMvc.perform(get("/pets/3/dashboard").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
		mockMvc.perform(get("/pets").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
	}

	private MockHttpSession autenticar(String usuario) throws Exception {
		String senha = UUID.randomUUID().toString();
		assertThat(jdbc.update("update tb_usuario set senha = ? where username = ?",
				encoder.encode(senha), usuario)).isEqualTo(1);
		return (MockHttpSession) mockMvc.perform(formLogin("/login").user(usuario).password(senha))
				.andExpect(authenticated())
				.andReturn()
				.getRequest()
				.getSession(false);
	}

}
