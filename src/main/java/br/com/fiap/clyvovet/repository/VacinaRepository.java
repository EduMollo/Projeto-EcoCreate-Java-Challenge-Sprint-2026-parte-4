package br.com.fiap.clyvovet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Vacina;

public interface VacinaRepository extends JpaRepository<Vacina, Long> {

	List<Vacina> findByPetIdOrderByDataAplicacaoDesc(Long idPet);

}
