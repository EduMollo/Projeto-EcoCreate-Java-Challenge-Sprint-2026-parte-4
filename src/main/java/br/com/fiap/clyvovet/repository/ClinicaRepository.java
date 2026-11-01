package br.com.fiap.clyvovet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Clinica;

public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

	List<Clinica> findAllByOrderByNome();

	List<Clinica> findByAtivoTrueOrderByNome();

	boolean existsByCnpj(String cnpj);

	boolean existsByCnpjAndIdNot(String cnpj, Long id);

}
