package br.com.fiap.clyvovet.validation;

import br.com.fiap.clyvovet.controller.form.ClinicaForm;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ClinicaUnicaValidator implements ConstraintValidator<ClinicaUnica, ClinicaForm> {

	private final ClinicaRepository clinicaRepository;

	public ClinicaUnicaValidator(ClinicaRepository clinicaRepository) {
		this.clinicaRepository = clinicaRepository;
	}

	@Override
	public boolean isValid(ClinicaForm form, ConstraintValidatorContext contexto) {
		long id = Violacoes.idOuInexistente(form.getId());
		if (form.getCnpj() != null && clinicaRepository.existsByCnpjAndIdNot(form.getCnpj(), id)) {
			Violacoes.emCampo(contexto, "cnpj", "Já existe uma clínica com este CNPJ.");
			return false;
		}
		return true;
	}

}
