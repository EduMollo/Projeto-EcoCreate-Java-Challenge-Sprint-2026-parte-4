package br.com.fiap.clyvovet.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;

class ConsultaTest {

	private static final LocalDate DATA = LocalDate.of(2026, 9, 11);

	@Test
	void deveSeguirJornadaAgendadaEmAndamentoConcluida() {
		Consulta consulta = novaConsulta(StatusConsulta.AGENDADA);

		consulta.iniciarAtendimento();
		assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.EM_ANDAMENTO);

		consulta.concluir("Otite externa.", "Limpeza auricular", null, new BigDecimal("150.00"), DATA.plusDays(15));
		assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONCLUIDA);
		assertThat(consulta.possuiRetornoPrevisto()).isTrue();
	}

	@Test
	void naoDeveConcluirConsultaQueNaoEstaEmAndamento() {
		Consulta consulta = novaConsulta(StatusConsulta.AGENDADA);

		assertThatThrownBy(() -> consulta.concluir("Diagnóstico", null, null, null, null))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessageContaining("em andamento");
	}

	@Test
	void naoDeveAceitarRetornoAnteriorOuIgualADataDaConsulta() {
		Consulta consulta = novaConsulta(StatusConsulta.EM_ANDAMENTO);

		assertThatThrownBy(() -> consulta.concluir("Diagnóstico", null, null, null, DATA))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessageContaining("retorno");
		assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.EM_ANDAMENTO);
	}

	@Test
	void deveCancelarConsultaAbertaGuardandoMotivo() {
		Consulta consulta = novaConsulta(StatusConsulta.AGENDADA);

		consulta.cancelar("Tutor desmarcou.");

		assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CANCELADA);
		assertThat(consulta.getMotivoCancelamento()).isEqualTo("Tutor desmarcou.");
	}

	@Test
	void naoDeveCancelarConsultaConcluidaNemJaCancelada() {
		assertThatThrownBy(() -> novaConsulta(StatusConsulta.CONCLUIDA).cancelar("motivo"))
				.isInstanceOf(RegraDeNegocioException.class);
		assertThatThrownBy(() -> novaConsulta(StatusConsulta.CANCELADA).cancelar("motivo"))
				.isInstanceOf(RegraDeNegocioException.class);
	}

	private Consulta novaConsulta(StatusConsulta status) {
		return Consulta.builder()
				.dataConsulta(DATA)
				.status(status)
				.tipoConsulta(TipoConsulta.ROTINA)
				.veterinario("Dra. Camila Rocha")
				.build();
	}

}
