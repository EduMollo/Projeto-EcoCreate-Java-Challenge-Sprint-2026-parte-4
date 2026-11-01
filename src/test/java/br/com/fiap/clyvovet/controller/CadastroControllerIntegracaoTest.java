package br.com.fiap.clyvovet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import br.com.fiap.clyvovet.repository.UsuarioRepository;

/**
 * Auto-cadastro pela tela de login: rota pública, validações e a conta criada já
 * consegue autenticar com perfil TUTOR.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CadastroControllerIntegracaoTest {

	private static final String FORM = "cadastroTutorForm";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Test
	void formularioDeCadastroEhPublico() throws Exception {
		mockMvc.perform(get("/cadastro"))
				.andExpect(status().isOk())
				.andExpect(view().name("cadastro"));
	}

	@Test
	void cadastroValidoCriaTutorEContaComPerfilTutorQueConsegueLogar() throws Exception {
		mockMvc.perform(cadastro("joao.silva", "segredo1", "segredo1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?cadastro=true"));

		Usuario criado = usuarioRepository.findByUsername("joao.silva").orElseThrow();
		assertThat(criado.possuiPerfil(PerfilEnum.TUTOR)).isTrue();
		assertThat(criado.getTutor().getCpf()).isEqualTo("11122233344");
		assertThat(criado.getNomeExibicao()).isEqualTo("João da Silva");

		MockHttpSession sessao = (MockHttpSession) mockMvc
				.perform(formLogin("/login").user("joao.silva").password("segredo1"))
				.andExpect(authenticated().withUsername("joao.silva"))
				.andReturn().getRequest().getSession(false);

		mockMvc.perform(get("/meus-pets").session(sessao)).andExpect(status().isOk());
		mockMvc.perform(get("/pets").session(sessao))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/acesso-negado"));
	}

	@Test
	void senhasDiferentesVoltamParaOFormularioComErro() throws Exception {
		mockMvc.perform(cadastro("maria.souza", "segredo1", "outra123"))
				.andExpect(status().isOk())
				.andExpect(view().name("cadastro"))
				.andExpect(model().attributeHasFieldErrors(FORM, "conta.senhasConferem"));

		assertThat(usuarioRepository.existsByUsername("maria.souza")).isFalse();
	}

	@Test
	void usernameJaExistenteEhRejeitado() throws Exception {
		mockMvc.perform(cadastro("admin", "segredo1", "segredo1"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasFieldErrors(FORM, "conta.username"));
	}

	@Test
	void cpfJaCadastradoComoTutorEhRejeitado() throws Exception {
		mockMvc.perform(cadastro("novo.usuario", "segredo1", "segredo1").param("tutor.cpf", "12345678901"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasFieldErrors(FORM, "tutor.cpf"));
	}

	private MockHttpServletRequestBuilder cadastro(String username, String senha, String confirmacao) {
		return post("/cadastro").with(csrf())
				.param("tutor.nome", "João da Silva")
				.param("tutor.cpf", "11122233344")
				.param("tutor.email", "joao.silva@email.com")
				.param("tutor.telefone", "11999990000")
				.param("tutor.dataNascimento", "1992-05-10")
				.param("conta.username", username)
				.param("conta.senha", senha)
				.param("conta.confirmacaoSenha", confirmacao);
	}

}
