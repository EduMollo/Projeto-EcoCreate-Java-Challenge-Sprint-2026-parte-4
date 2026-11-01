package br.com.fiap.clyvovet.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Único ponto de leitura do usuário autenticado. Evita espalhar
 * {@code SecurityContextHolder} por controllers e services.
 */
@Component
public class UsuarioLogadoProvider {

	public Optional<UsuarioAutenticado> obter() {
		Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
		if (autenticacao != null && autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario) {
			return Optional.of(usuario);
		}
		return Optional.empty();
	}

	public UsuarioAutenticado exigir() {
		return obter().orElseThrow(() -> new IllegalStateException("Nenhum usuário autenticado."));
	}

}
