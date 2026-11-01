package br.com.fiap.clyvovet.validation;

import br.com.fiap.clyvovet.controller.form.TutorForm;
import br.com.fiap.clyvovet.repository.TutorRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TutorUnicoValidator implements ConstraintValidator<TutorUnico, TutorForm> {

	private final TutorRepository tutorRepository;

	public TutorUnicoValidator(TutorRepository tutorRepository) {
		this.tutorRepository = tutorRepository;
	}

	@Override
	public boolean isValid(TutorForm form, ConstraintValidatorContext contexto) {
		long id = Violacoes.idOuInexistente(form.getId());
		boolean valido = true;

		if (form.getCpf() != null && tutorRepository.existsByCpfAndIdNot(form.getCpf(), id)) {
			Violacoes.emCampo(contexto, "cpf", "Já existe um tutor com este CPF.");
			valido = false;
		}
		if (form.getEmail() != null && tutorRepository.existsByEmailAndIdNot(form.getEmail(), id)) {
			Violacoes.emCampo(contexto, "email", "Já existe um tutor com este e-mail.");
			valido = false;
		}
		return valido;
	}

}
