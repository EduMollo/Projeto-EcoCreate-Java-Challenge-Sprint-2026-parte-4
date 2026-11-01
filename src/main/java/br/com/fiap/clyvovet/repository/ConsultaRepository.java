package br.com.fiap.clyvovet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	@EntityGraph(attributePaths = { "pet", "pet.tutor", "clinica" })
	Optional<Consulta> findDetalhadaById(Long id);

	@EntityGraph(attributePaths = { "pet", "pet.tutor", "clinica" })
	List<Consulta> findAllByOrderByDataConsultaDescIdDesc();

	@EntityGraph(attributePaths = { "pet", "pet.tutor", "clinica" })
	List<Consulta> findByStatusOrderByDataConsultaAscIdAsc(StatusConsulta status);

	@EntityGraph(attributePaths = { "pet", "pet.tutor", "clinica" })
	List<Consulta> findByDataConsultaAndStatusInOrderByPetNome(LocalDate data, List<StatusConsulta> status);

	@EntityGraph(attributePaths = { "pet", "clinica" })
	List<Consulta> findByPetIdOrderByDataConsultaDescIdDesc(Long idPet);

	@EntityGraph(attributePaths = { "pet", "pet.tutor", "clinica" })
	List<Consulta> findByStatusAndDataConsultaBeforeOrderByDataConsulta(StatusConsulta status, LocalDate data);

	long countByDataConsultaAndStatusIn(LocalDate data, List<StatusConsulta> status);

}
