package br.com.fiap.clyvovet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.MedicamentoForm;
import br.com.fiap.clyvovet.controller.form.VacinaForm;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Medicamento;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.model.enums.TipoEvento;
import br.com.fiap.clyvovet.repository.MedicamentoRepository;
import br.com.fiap.clyvovet.repository.VacinaRepository;

/**
 * Registros clínicos derivados de uma consulta: medicamentos prescritos e vacinas aplicadas.
 */
@Service
@Transactional
public class PrescricaoService {

	private final ConsultaService consultaService;
	private final MedicamentoRepository medicamentoRepository;
	private final VacinaRepository vacinaRepository;
	private final LinhaDoTempoService linhaDoTempoService;

	public PrescricaoService(ConsultaService consultaService, MedicamentoRepository medicamentoRepository,
			VacinaRepository vacinaRepository, LinhaDoTempoService linhaDoTempoService) {
		this.consultaService = consultaService;
		this.medicamentoRepository = medicamentoRepository;
		this.vacinaRepository = vacinaRepository;
		this.linhaDoTempoService = linhaDoTempoService;
	}

	@Transactional(readOnly = true)
	public List<Medicamento> medicamentosDaConsulta(Long idConsulta) {
		return medicamentoRepository.findByConsultaIdOrderByNome(idConsulta);
	}

	public void prescreverMedicamento(Long idConsulta, MedicamentoForm form) {
		Consulta consulta = consultaEmAtendimento(idConsulta);

		medicamentoRepository.save(Medicamento.builder()
				.pet(consulta.getPet())
				.consulta(consulta)
				.nome(form.getNome().trim())
				.principioAtivo(form.getPrincipioAtivo())
				.dosagem(form.getDosagem().trim())
				.frequencia(form.getFrequencia().trim())
				.dataInicio(form.getDataInicio())
				.dataFim(form.getDataFim())
				.usoContinuo(form.isUsoContinuo())
				.observacoes(form.getObservacoes())
				.build());

		linhaDoTempoService.registrar(consulta, TipoEvento.MEDICAMENTO,
				"Medicamento prescrito: " + form.getNome().trim() + " — " + form.getDosagem().trim() + ", "
						+ form.getFrequencia().trim() + ".", form.getDataFim());
	}

	public void registrarVacina(Long idConsulta, VacinaForm form) {
		Consulta consulta = consultaEmAtendimento(idConsulta);

		vacinaRepository.save(Vacina.builder()
				.pet(consulta.getPet())
				.clinica(consulta.getClinica())
				.nome(form.getNome().trim())
				.fabricante(form.getFabricante().trim())
				.lote(form.getLote())
				.dataAplicacao(form.getDataAplicacao())
				.dataValidade(form.getDataValidade())
				.proximaDose(form.getProximaDose())
				.build());

		linhaDoTempoService.registrar(consulta, TipoEvento.VACINA,
				"Vacina aplicada: " + form.getNome().trim() + " (" + form.getFabricante().trim() + ").",
				form.getProximaDose());
	}

	private Consulta consultaEmAtendimento(Long idConsulta) {
		Consulta consulta = consultaService.buscar(idConsulta);
		if (!consulta.isEmAndamento() && !consulta.isConcluida()) {
			throw new RegraDeNegocioException(
					"Só é possível registrar prescrições em consultas em andamento ou concluídas.");
		}
		return consulta;
	}

}
