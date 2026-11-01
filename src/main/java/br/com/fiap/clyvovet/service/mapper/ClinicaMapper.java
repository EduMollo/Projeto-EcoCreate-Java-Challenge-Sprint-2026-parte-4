package br.com.fiap.clyvovet.service.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.controller.form.ClinicaForm;
import br.com.fiap.clyvovet.model.Clinica;

@Component
public class ClinicaMapper {

	public Clinica paraEntidade(ClinicaForm form) {
		Clinica clinica = new Clinica();
		atualizar(clinica, form);
		return clinica;
	}

	public void atualizar(Clinica clinica, ClinicaForm form) {
		clinica.setNome(form.getNome().trim());
		clinica.setCnpj(form.getCnpj());
		clinica.setTelefone(form.getTelefone());
		clinica.setEmail(form.getEmail().trim().toLowerCase());
		clinica.setEndereco(form.getEndereco().trim());
		clinica.setCidade(form.getCidade().trim());
		clinica.setEstado(form.getEstado());
	}

	public ClinicaForm paraForm(Clinica clinica) {
		ClinicaForm form = new ClinicaForm();
		form.setId(clinica.getId());
		form.setNome(clinica.getNome());
		form.setCnpj(clinica.getCnpj());
		form.setTelefone(clinica.getTelefone());
		form.setEmail(clinica.getEmail());
		form.setEndereco(clinica.getEndereco());
		form.setCidade(clinica.getCidade());
		form.setEstado(clinica.getEstado());
		return form;
	}

}
