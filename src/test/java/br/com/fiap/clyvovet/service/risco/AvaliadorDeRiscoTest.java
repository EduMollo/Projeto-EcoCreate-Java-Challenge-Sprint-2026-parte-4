package br.com.fiap.clyvovet.service.risco;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.model.enums.Especie;
import br.com.fiap.clyvovet.model.enums.Prioridade;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;

/**
 * Testa o conjunto de estratégias sem Spring: as regras são instanciadas à mão,
 * exatamente como o container faria ao injetar {@code List<RegraDeRisco>}.
 */
class AvaliadorDeRiscoTest {

	private static final LocalDate HOJE = LocalDate.of(2026, 9, 11);

	private final AvaliadorDeRisco avaliador = new AvaliadorDeRisco(List.of(
			new SemConsultaRegistradaRegra(),
			new ConsultaAtrasadaRegra(),
			new VacinaVencidaRegra(),
			new DoseAtrasadaRegra(),
			new ConsultaNaoRealizadaRegra(),
			new PetSeniorRegra()));

	@Test
	void petJovemComAcompanhamentoEmDiaNaoGeraAlertas() {
		Pet pet = pet(3);
		Consulta recente = consulta(pet, StatusConsulta.CONCLUIDA, HOJE.minusDays(30));
		Vacina valida = vacina(pet, HOJE.minusDays(30), HOJE.plusDays(335));

		ResultadoRisco resultado = avaliador.avaliar(new ContextoSaudePet(pet, List.of(recente), List.of(valida), List.of(), HOJE));

		assertThat(resultado.getScore()).isZero();
		assertThat(resultado.getClassificacao()).isEqualTo(ClassificacaoRisco.BAIXO);
		assertThat(resultado.getAlertas()).isEmpty();
	}

	@Test
	void petSeniorSemConsultaEComVacinaVencidaAcumulaPontosDeCadaRegra() {
		Pet pet = pet(8);
		Vacina vencida = vacina(pet, HOJE.minusDays(400), HOJE.minusDays(35));

		ResultadoRisco resultado = avaliador.avaliar(new ContextoSaudePet(pet, List.of(), List.of(vencida), List.of(), HOJE));

		// 40 (sem consulta) + 10 (1 vacina vencida) + 10 (sênior 7-9 anos) + 10 (dose atrasada)
		assertThat(resultado.getScore()).isEqualTo(70);
		assertThat(resultado.getClassificacao()).isEqualTo(ClassificacaoRisco.ALTO);
		assertThat(resultado.getAlertas()).extracting(Alerta::getTitulo)
				.containsExactlyInAnyOrder("Sem consulta registrada", "Vacina vencida", "Dose de reforço atrasada", "Pet sênior");
	}

	@Test
	void alertasSaoOrdenadosPorPrioridadeComUrgentePrimeiro() {
		Pet pet = pet(2);
		Vacina vencida = vacina(pet, HOJE.minusDays(400), HOJE.minusDays(1));

		ResultadoRisco resultado = avaliador.avaliar(new ContextoSaudePet(pet, List.of(), List.of(vencida), List.of(), HOJE));

		assertThat(resultado.getAlertas().get(0).getPrioridade()).isEqualTo(Prioridade.URGENTE);
	}

	@Test
	void scoreNuncaUltrapassaCem() {
		Pet pet = pet(12);
		List<Vacina> vencidas = List.of(vacina(pet, HOJE.minusDays(800), HOJE.minusDays(400)),
				vacina(pet, HOJE.minusDays(800), HOJE.minusDays(400)),
				vacina(pet, HOJE.minusDays(800), HOJE.minusDays(400)));
		Consulta antiga = consulta(pet, StatusConsulta.CONCLUIDA, HOJE.minusDays(700));
		Consulta naoRealizada = consulta(pet, StatusConsulta.AGENDADA, HOJE.minusDays(10));

		ResultadoRisco resultado = avaliador.avaliar(
				new ContextoSaudePet(pet, List.of(antiga, naoRealizada), vencidas, List.of(), HOJE));

		assertThat(resultado.getScore()).isEqualTo(100);
		assertThat(resultado.getClassificacao()).isEqualTo(ClassificacaoRisco.ALTO);
	}

	private Pet pet(int idadeAnos) {
		return Pet.builder().nome("Thor").especie(Especie.CACHORRO).raca("Labrador")
				.dataNascimento(HOJE.minusYears(idadeAnos)).build();
	}

	private Consulta consulta(Pet pet, StatusConsulta status, LocalDate data) {
		return Consulta.builder().pet(pet).status(status).dataConsulta(data)
				.tipoConsulta(TipoConsulta.ROTINA).veterinario("Dra. Camila").build();
	}

	private Vacina vacina(Pet pet, LocalDate aplicacao, LocalDate validade) {
		return Vacina.builder().pet(pet).nome("V10").fabricante("Zoetis")
				.dataAplicacao(aplicacao).dataValidade(validade).proximaDose(validade).build();
	}

}
