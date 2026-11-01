package br.com.fiap.clyvovet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

	List<Tutor> findAllByOrderByNome();

	List<Tutor> findByAtivoTrueOrderByNome();

	boolean existsByCpf(String cpf);

	boolean existsByCpfAndIdNot(String cpf, Long id);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

}
