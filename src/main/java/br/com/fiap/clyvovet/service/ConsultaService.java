package br.com.fiap.clyvovet.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.AgendamentoForm;
import br.com.fiap.clyvovet.controller.form.AtendimentoForm;
import br.com.fiap.clyvovet.controller.form.CancelamentoForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;
import br.com.fiap.clyvovet.model.enums.TipoEvento;
import br.com.fiap.clyvovet.repository.ConsultaRepository;

/**
 * Jornada do atendimento: agendar → iniciar → concluir (com retorno automático) / cancelar.
 * As transições de estado vivem na entidade; aqui ficam a orquestração e os efeitos
 * colaterais (linha do tempo, agendamento do retorno).
 */
@Service
@Transactional
public class ConsultaService {

	private static final List<StatusConsulta> STATUS_ABERTOS = List.of(StatusConsulta.AGENDADA,
			StatusConsulta.EM_ANDAMENTO);

	private final ConsultaRepository consultaRepository;
	private final PetService petService;
	private final ClinicaService clinicaService;
	private final LinhaDoTempoService linhaDoTempoService;

	public ConsultaService(ConsultaRepository consultaRepository, PetService petService,
			ClinicaService clinicaService, LinhaDoTempoService linhaDoTempoService) {
		this.consultaRepository = consultaRepository;
		this.petService = petService;
		this.clinicaService = clinicaService;
		this.linhaDoTempoService = linhaDoTempoService;
	}

	@Transactional(readOnly = true)
	public List<Consulta> listar(StatusConsulta filtro) {
		return filtro == null
				? consultaRepository.findAllByOrderByDataConsultaDescIdDesc()
				: consultaRepository.findByStatusOrderByDataConsultaAscIdAsc(filtro);
	}

	@Transactional(readOnly = true)
	public List<Consulta> listarDoPet(Long idPet) {
		petService.buscar(idPet);
		return consultaRepository.findByPetIdOrderByDataConsultaDescIdDesc(idPet);
	}

	@Transactional(readOnly = true)
	public List<Consulta> agendaDoDia() {
		return consultaRepository.findByDataConsultaAndStatusInOrderByPetNome(LocalDate.now(), STATUS_ABERTOS);
	}

	@Transactional(readOnly = true)
	public List<Consulta> agendadasNaoRealizadas() {
		return consultaRepository.findByStatusAndDataConsultaBeforeOrderByDataConsulta(StatusConsulta.AGENDADA,
				LocalDate.now());
	}

	@Transactional(readOnly = true)
	public long contarAgendaDoDia() {
		return consultaRepository.countByDataConsultaAndStatusIn(LocalDate.now(), STATUS_ABERTOS);
	}

	@Transactional(readOnly = true)
	public Consulta buscar(Long id) {
		Consulta consulta = consultaRepository.findDetalhadaById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", id));
		petService.buscar(consulta.getPet().getId());
		return consulta;
	}

	public Consulta agendar(AgendamentoForm form) {
		Consulta consulta = Consulta.builder()
				.pet(petService.buscar(form.getIdPet()))
				.clinica(clinicaService.buscar(form.getIdClinica()))
				.dataConsulta(form.getDataConsulta())
				.tipoConsulta(form.getTipoConsulta())
				.veterinario(form.getVeterinario().trim())
				.observacoes(form.getObservacoes())
				.status(StatusConsulta.AGENDADA)
				.build();
		return consultaRepository.save(consulta);
	}

	public void iniciarAtendimento(Long id) {
		buscar(id).iniciarAtendimento();
	}

	public void concluir(Long id, AtendimentoForm form) {
		Consulta consulta = buscar(id);
		consulta.concluir(form.getDiagnostico(), form.getPrescricao(), form.getObservacoes(), form.getValor(),
				form.getDataRetorno());

		linhaDoTempoService.registrar(consulta, TipoEvento.CONSULTA,
				"Consulta " + consulta.getTipoConsulta().getDescricao().toLowerCase() + " concluída. Diagnóstico: "
						+ consulta.getDiagnostico());

		if (consulta.possuiRetornoPrevisto()) {
			agendarRetorno(consulta);
		}
	}

	public void cancelar(Long id, CancelamentoForm form) {
		buscar(id).cancelar(form.getMotivo().trim());
	}

	private void agendarRetorno(Consulta origem) {
		Consulta retorno = Consulta.builder()
				.pet(origem.getPet())
				.clinica(origem.getClinica())
				.dataConsulta(origem.getDataRetorno())
				.tipoConsulta(TipoConsulta.RETORNO)
				.veterinario(origem.getVeterinario())
				.status(StatusConsulta.AGENDADA)
				.build();
		consultaRepository.save(retorno);

		linhaDoTempoService.registrar(origem, TipoEvento.RETORNO,
				"Retorno agendado automaticamente para " + origem.getDataRetorno() + ".", origem.getDataRetorno());
	}

}
