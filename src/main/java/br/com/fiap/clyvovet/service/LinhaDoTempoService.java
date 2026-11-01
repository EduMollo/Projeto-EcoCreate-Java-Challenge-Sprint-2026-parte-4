package br.com.fiap.clyvovet.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.EventoSaude;
import br.com.fiap.clyvovet.model.enums.TipoEvento;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;

/**
 * Mantém a linha do tempo do pet. Cada passo da jornada (consulta concluída,
 * medicamento prescrito, vacina aplicada, retorno previsto) vira um evento.
 */
@Service
@Transactional
public class LinhaDoTempoService {

	private final EventoSaudeRepository eventoSaudeRepository;

	public LinhaDoTempoService(EventoSaudeRepository eventoSaudeRepository) {
		this.eventoSaudeRepository = eventoSaudeRepository;
	}

	public void registrar(Consulta consulta, TipoEvento tipo, String descricao, LocalDate dataProximaAcao) {
		eventoSaudeRepository.save(EventoSaude.builder()
				.pet(consulta.getPet())
				.consulta(consulta)
				.tipoEvento(tipo)
				.descricao(descricao)
				.dataEvento(LocalDate.now())
				.dataProximaAcao(dataProximaAcao)
				.build());
	}

	public void registrar(Consulta consulta, TipoEvento tipo, String descricao) {
		registrar(consulta, tipo, descricao, null);
	}

	@Transactional(readOnly = true)
	public List<EventoSaude> listarDoPet(Long idPet) {
		return eventoSaudeRepository.findByPetIdOrderByDataEventoDescIdDesc(idPet);
	}

}
