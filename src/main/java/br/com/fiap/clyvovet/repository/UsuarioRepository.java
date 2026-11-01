package br.com.fiap.clyvovet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@EntityGraph(attributePaths = { "perfis", "tutor" })
	Optional<Usuario> findByUsername(String username);

	boolean existsByUsername(String username);

	@EntityGraph(attributePaths = { "perfis", "tutor" })
	List<Usuario> findAllByOrderByNomeExibicao();

}
