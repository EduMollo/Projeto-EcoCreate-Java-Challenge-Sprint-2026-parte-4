package br.com.fiap.clyvovet.validation;

import br.com.fiap.clyvovet.controller.form.PetForm;
import br.com.fiap.clyvovet.repository.PetRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PetUnicoValidator implements ConstraintValidator<PetUnico, PetForm> {

	private final PetRepository petRepository;

	public PetUnicoValidator(PetRepository petRepository) {
		this.petRepository = petRepository;
	}

	@Override
	public boolean isValid(PetForm form, ConstraintValidatorContext contexto) {
		String microchip = form.getMicrochip();
		if (microchip == null || microchip.isBlank()) {
			return true;
		}
		if (petRepository.existsByMicrochipAndIdNot(microchip, Violacoes.idOuInexistente(form.getId()))) {
			Violacoes.emCampo(contexto, "microchip", "Já existe um pet com este microchip.");
			return false;
		}
		return true;
	}

}
