package br.com.fiap.clyvovet.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.security.UsuarioAutenticado;
import br.com.fiap.clyvovet.security.UsuarioLogadoProvider;

/**
 * Autorização por posse: perfil libera a rota, mas um tutor só enxerga os
 * próprios pets. A checagem fica na camada de serviço para valer em qualquer
 * caminho que leve ao pet (detalhes, dashboard, histórico, consultas).
 */
@Service
public class AcessoPetService {

	private final UsuarioLogadoProvider usuarioLogadoProvider;

	public AcessoPetService(UsuarioLogadoProvider usuarioLogadoProvider) {
		this.usuarioLogadoProvider = usuarioLogadoProvider;
	}

	public void garantirAcesso(Pet pet) {
		UsuarioAutenticado usuario = usuarioLogadoProvider.exigir();
		if (usuario.isEquipeClinica()) {
			return;
		}
		if (!pet.pertenceAoTutor(usuario.getIdTutor())) {
			throw new AccessDeniedException("O pet " + pet.getId() + " não pertence ao tutor autenticado.");
		}
	}

	public Long idTutorLogado() {
		return usuarioLogadoProvider.exigir().getIdTutor();
	}

}
