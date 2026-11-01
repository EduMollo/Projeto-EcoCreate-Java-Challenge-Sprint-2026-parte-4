package br.com.fiap.clyvovet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

	Optional<Perfil> findByDescricao(PerfilEnum descricao);

}
