package br.com.fiap.clyvovet.service.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.controller.form.PetForm;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;

@Component
public class PetMapper {

	public Pet paraEntidade(PetForm form, Tutor tutor, Clinica clinicaPrincipal) {
		Pet pet = new Pet();
		atualizar(pet, form, tutor, clinicaPrincipal);
		return pet;
	}

	public void atualizar(Pet pet, PetForm form, Tutor tutor, Clinica clinicaPrincipal) {
		pet.setNome(form.getNome().trim());
		pet.setEspecie(form.getEspecie());
		pet.setRaca(form.getRaca().trim());
		pet.setDataNascimento(form.getDataNascimento());
		pet.setPesoKg(form.getPesoKg());
		pet.setSexo(form.getSexo());
		pet.setCastrado(form.getCastrado());
		pet.setMicrochip(vazioComoNulo(form.getMicrochip()));
		pet.setObservacoes(vazioComoNulo(form.getObservacoes()));
		pet.setTutor(tutor);
		pet.setClinicaPrincipal(clinicaPrincipal);
	}

	public PetForm paraForm(Pet pet) {
		PetForm form = new PetForm();
		form.setId(pet.getId());
		form.setNome(pet.getNome());
		form.setEspecie(pet.getEspecie());
		form.setRaca(pet.getRaca());
		form.setDataNascimento(pet.getDataNascimento());
		form.setPesoKg(pet.getPesoKg());
		form.setSexo(pet.getSexo());
		form.setCastrado(pet.getCastrado());
		form.setMicrochip(pet.getMicrochip());
		form.setObservacoes(pet.getObservacoes());
		form.setIdTutor(pet.getTutor().getId());
		form.setIdClinicaPrincipal(pet.getClinicaPrincipal() == null ? null : pet.getClinicaPrincipal().getId());
		return form;
	}

	private String vazioComoNulo(String valor) {
		return valor == null || valor.isBlank() ? null : valor.trim();
	}

}
