package br.com.fiap.clyvovet.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Medicamento;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
import br.com.fiap.clyvovet.repository.MedicamentoRepository;
import br.com.fiap.clyvovet.repository.VacinaRepository;
import br.com.fiap.clyvovet.service.risco.AvaliadorDeRisco;
import br.com.fiap.clyvovet.service.risco.ContextoSaudePet;
import br.com.fiap.clyvovet.service.risco.ResultadoRisco;

@Service
@Transactional(readOnly = true)
public class SaudePetService {

	private final PetService petService;
	private final ConsultaRepository consultaRepository;
	private final VacinaRepository vacinaRepository;
	private final MedicamentoRepository medicamentoRepository;
	private final EventoSaudeRepository eventoSaudeRepository;
	private final AvaliadorDeRisco avaliadorDeRisco;

	public SaudePetService(PetService petService, ConsultaRepository consultaRepository,
			VacinaRepository vacinaRepository, MedicamentoRepository medicamentoRepository,
			EventoSaudeRepository eventoSaudeRepository, AvaliadorDeRisco avaliadorDeRisco) {
		this.petService = petService;
		this.consultaRepository = consultaRepository;
		this.vacinaRepository = vacinaRepository;
		this.medicamentoRepository = medicamentoRepository;
		this.eventoSaudeRepository = eventoSaudeRepository;
		this.avaliadorDeRisco = avaliadorDeRisco;
	}

	public DashboardSaude gerarDashboard(Long idPet) {
		Pet pet = petService.buscar(idPet);
		LocalDate hoje = LocalDate.now();

		List<Consulta> consultas = consultaRepository.findByPetIdOrderByDataConsultaDescIdDesc(idPet);
		List<Vacina> vacinas = vacinaRepository.findByPetIdOrderByDataAplicacaoDesc(idPet);
		List<Medicamento> medicamentos = medicamentoRepository.findByPetIdOrderByDataInicioDesc(idPet);

		ContextoSaudePet contexto = new ContextoSaudePet(pet, consultas, vacinas, medicamentos, hoje);
		ResultadoRisco risco = avaliadorDeRisco.avaliar(contexto);

		return DashboardSaude.builder()
				.pet(pet)
				.risco(risco)
				.ultimaConsulta(contexto.dataUltimaConsultaConcluida().orElse(null))
				.diasDesdeUltimaConsulta(contexto.diasDesdeUltimaConsulta().orElse(null))
				.totalConsultas(consultas.size())
				.proximasConsultas(consultas.stream().filter(Consulta::isAgendada).toList())
				.vacinas(vacinas)
				.medicamentosAtivos(contexto.medicamentosAtivos())
				.ultimosEventos(eventoSaudeRepository.findTop10ByPetIdOrderByDataEventoDescIdDesc(idPet))
				.build();
	}

}
