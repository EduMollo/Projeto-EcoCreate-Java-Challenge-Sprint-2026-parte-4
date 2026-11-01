package br.com.fiap.clyvovet.validation;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Apoio aos validadores de classe: anexa a violação ao campo certo para que o
 * erro apareça embaixo do input correspondente ({@code th:errors}).
 */
final class Violacoes {

	private Violacoes() {
	}

	static void emCampo(ConstraintValidatorContext contexto, String campo, String mensagem) {
		contexto.disableDefaultConstraintViolation();
		contexto.buildConstraintViolationWithTemplate(mensagem)
				.addPropertyNode(campo)
				.addConstraintViolation();
	}

	static long idOuInexistente(Long id) {
		return id == null ? -1L : id;
	}

}
