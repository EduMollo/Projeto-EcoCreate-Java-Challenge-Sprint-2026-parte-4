package br.com.fiap.clyvovet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

	@EntityGraph(attributePaths = { "tutor", "clinicaPrincipal" })
	List<Pet> findByAtivoTrueOrderByNome();

	@EntityGraph(attributePaths = { "tutor", "clinicaPrincipal" })
	List<Pet> findByTutorIdAndAtivoTrueOrderByNome(Long idTutor);

	@EntityGraph(attributePaths = { "tutor", "clinicaPrincipal" })
	Optional<Pet> findDetalhadoById(Long id);

	boolean existsByMicrochip(String microchip);

	boolean existsByMicrochipAndIdNot(String microchip, Long id);

	long countByAtivoTrue();

}
