package br.com.fiap.clyvovet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.AtendimentoForm;
import br.com.fiap.clyvovet.controller.form.CancelamentoForm;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.EventoSaude;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;
import br.com.fiap.clyvovet.model.enums.TipoEvento;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import br.com.fiap.clyvovet.security.UsuarioAutenticado;

/**
 * Jornada do atendimento de ponta a ponta sobre os dados semeados pelo Flyway.
 * Cada teste roda em transação revertida ao final, então o banco volta ao estado inicial.
 */
@SpringBootTest
@Transactional
class ConsultaServiceIntegracaoTest {

	private static final long CONSULTA_NINA_AGENDADA_HOJE = 7L;
	private static final long CONSULTA_THOR_CONCLUIDA = 1L;
	private static final long CONSULTA_THOR_FUTURA = 3L;
	private static final long PET_NINA = 5L;

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private ConsultaRepository consultaRepository;

	@Autowired
	private EventoSaudeRepository eventoSaudeRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeEach
	void autenticarComoVeterinario() {
		UsuarioAutenticado veterinario = new UsuarioAutenticado(usuarioRepository.findByUsername("vet").orElseThrow());
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(veterinario, null, veterinario.getAuthorities()));
	}

	@AfterEach
	void limparAutenticacao() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void concluirComRetornoAgendaNovaConsultaEGeraEventosNaLinhaDoTempo() {
		LocalDate dataRetorno = LocalDate.now().plusDays(14);
		consultaService.iniciarAtendimento(CONSULTA_NINA_AGENDADA_HOJE);

		consultaService.concluir(CONSULTA_NINA_AGENDADA_HOJE, atendimento("Saudável. Bico e penas em bom estado.", dataRetorno));

		Consulta concluida = consultaRepository.findById(CONSULTA_NINA_AGENDADA_HOJE).orElseThrow();
		assertThat(concluida.getStatus()).isEqualTo(StatusConsulta.CONCLUIDA);
		assertThat(concluida.getValor()).isEqualByComparingTo("90.00");

		List<Consulta> consultasDaNina = consultaRepository.findByPetIdOrderByDataConsultaDescIdDesc(PET_NINA);
		assertThat(consultasDaNina).hasSize(2);
		Consulta retorno = consultasDaNina.get(0);
		assertThat(retorno.getTipoConsulta()).isEqualTo(TipoConsulta.RETORNO);
		assertThat(retorno.getStatus()).isEqualTo(StatusConsulta.AGENDADA);
		assertThat(retorno.getDataConsulta()).isEqualTo(dataRetorno);
		assertThat(retorno.getVeterinario()).isEqualTo(concluida.getVeterinario());

		assertThat(eventoSaudeRepository.findByPetIdOrderByDataEventoDescIdDesc(PET_NINA))
				.extracting(EventoSaude::getTipoEvento)
				.containsExactlyInAnyOrder(TipoEvento.CONSULTA, TipoEvento.RETORNO);
	}

	@Test
	void concluirSemRetornoNaoAgendaNada() {
		consultaService.iniciarAtendimento(CONSULTA_NINA_AGENDADA_HOJE);

		consultaService.concluir(CONSULTA_NINA_AGENDADA_HOJE, atendimento("Saudável.", null));

		assertThat(consultaRepository.findByPetIdOrderByDataConsultaDescIdDesc(PET_NINA)).hasSize(1);
		assertThat(eventoSaudeRepository.findByPetIdOrderByDataEventoDescIdDesc(PET_NINA))
				.extracting(EventoSaude::getTipoEvento)
				.containsExactly(TipoEvento.CONSULTA);
	}

	@Test
	void retornoNaMesmaDataDaConsultaEhRejeitado() {
		consultaService.iniciarAtendimento(CONSULTA_NINA_AGENDADA_HOJE);

		assertThatThrownBy(() -> consultaService.concluir(CONSULTA_NINA_AGENDADA_HOJE, atendimento("Saudável.", LocalDate.now())))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessageContaining("retorno");
	}

	@Test
	void cancelamentoSoValeParaConsultaAbertaEGuardaMotivo() {
		CancelamentoForm cancelamento = new CancelamentoForm();
		cancelamento.setMotivo("Tutor desmarcou por viagem.");

		assertThatThrownBy(() -> consultaService.cancelar(CONSULTA_THOR_CONCLUIDA, cancelamento))
				.isInstanceOf(RegraDeNegocioException.class);

		consultaService.cancelar(CONSULTA_THOR_FUTURA, cancelamento);

		Consulta cancelada = consultaRepository.findById(CONSULTA_THOR_FUTURA).orElseThrow();
		assertThat(cancelada.getStatus()).isEqualTo(StatusConsulta.CANCELADA);
		assertThat(cancelada.getMotivoCancelamento()).isEqualTo("Tutor desmarcou por viagem.");
	}

	private AtendimentoForm atendimento(String diagnostico, LocalDate dataRetorno) {
		AtendimentoForm form = new AtendimentoForm();
		form.setDiagnostico(diagnostico);
		form.setValor(new BigDecimal("90.00"));
		form.setDataRetorno(dataRetorno);
		return form;
	}

}
