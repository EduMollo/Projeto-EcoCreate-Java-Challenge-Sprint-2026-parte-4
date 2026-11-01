package br.com.fiap.clyvovet.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.fiap.clyvovet.security.UsuarioAutenticado;
import br.com.fiap.clyvovet.security.UsuarioLogadoProvider;

/**
 * Disponibiliza {@code usuarioLogado} para todas as views (navbar, saudação,
 * menus condicionais por perfil) sem repetir esse código em cada controller.
 */
@ControllerAdvice
public class UsuarioLogadoControllerAdvice {

	private final UsuarioLogadoProvider usuarioLogadoProvider;

	public UsuarioLogadoControllerAdvice(UsuarioLogadoProvider usuarioLogadoProvider) {
		this.usuarioLogadoProvider = usuarioLogadoProvider;
	}

	@ModelAttribute("usuarioLogado")
	public UsuarioAutenticado usuarioLogado() {
		return usuarioLogadoProvider.obter().orElse(null);
	}

}
