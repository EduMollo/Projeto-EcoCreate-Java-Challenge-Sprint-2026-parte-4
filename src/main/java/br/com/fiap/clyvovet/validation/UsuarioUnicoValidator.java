package br.com.fiap.clyvovet.validation;

import br.com.fiap.clyvovet.controller.form.ContaForm;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsuarioUnicoValidator implements ConstraintValidator<UsuarioUnico, ContaForm> {

	private final UsuarioRepository usuarioRepository;

	public UsuarioUnicoValidator(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public boolean isValid(ContaForm form, ConstraintValidatorContext contexto) {
		String username = form.getUsername();
		if (username != null && usuarioRepository.existsByUsername(username.trim().toLowerCase())) {
			Violacoes.emCampo(contexto, "username", "Este nome de usuário já está em uso.");
			return false;
		}
		return true;
	}

}
