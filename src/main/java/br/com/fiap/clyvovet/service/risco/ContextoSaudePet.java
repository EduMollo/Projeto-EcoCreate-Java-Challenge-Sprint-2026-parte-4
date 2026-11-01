package br.com.fiap.clyvovet.service.risco;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Medicamento;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Vacina;

/**
 * Fotografia do histórico do pet na data de referência. As regras de risco só
 * enxergam este objeto, o que as mantém puras e testáveis sem banco.
 */
public record ContextoSaudePet(Pet pet, List<Consulta> consultas, List<Vacina> vacinas,
		List<Medicamento> medicamentos, LocalDate hoje) {

	public Optional<LocalDate> dataUltimaConsultaConcluida() {
		return consultas.stream()
				.filter(Consulta::isConcluida)
				.map(Consulta::getDataConsulta)
				.max(LocalDate::compareTo);
	}

	public Optional<Long> diasDesdeUltimaConsulta() {
		return dataUltimaConsultaConcluida().map(data -> ChronoUnit.DAYS.between(data, hoje));
	}

	public long totalVacinasVencidas() {
		return vacinas.stream().filter(vacina -> vacina.isVencida(hoje)).count();
	}

	public long totalDosesAtrasadas() {
		return vacinas.stream().filter(vacina -> vacina.isDoseAtrasada(hoje)).count();
	}

	public long totalConsultasNaoRealizadas() {
		return consultas.stream()
				.filter(Consulta::isAgendada)
				.filter(consulta -> consulta.getDataConsulta().isBefore(hoje))
				.count();
	}

	public List<Medicamento> medicamentosAtivos() {
		return medicamentos.stream().filter(medicamento -> medicamento.isAtivo(hoje)).toList();
	}

	public int idadeDoPet() {
		return pet.getIdadeEmAnos();
	}

}
