package br.com.fiap.clyvovet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.EventoSaude;

public interface EventoSaudeRepository extends JpaRepository<EventoSaude, Long> {

	List<EventoSaude> findByPetIdOrderByDataEventoDescIdDesc(Long idPet);

	List<EventoSaude> findTop10ByPetIdOrderByDataEventoDescIdDesc(Long idPet);

}
