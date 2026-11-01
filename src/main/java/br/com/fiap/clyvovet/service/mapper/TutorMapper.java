package br.com.fiap.clyvovet.service.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.controller.form.TutorForm;
import br.com.fiap.clyvovet.model.Tutor;

@Component
public class TutorMapper {

	public Tutor paraEntidade(TutorForm form) {
		Tutor tutor = new Tutor();
		atualizar(tutor, form);
		return tutor;
	}

	public void atualizar(Tutor tutor, TutorForm form) {
		tutor.setNome(form.getNome().trim());
		tutor.setCpf(form.getCpf());
		tutor.setEmail(form.getEmail().trim().toLowerCase());
		tutor.setTelefone(form.getTelefone());
		tutor.setDataNascimento(form.getDataNascimento());
	}

	public TutorForm paraForm(Tutor tutor) {
		TutorForm form = new TutorForm();
		form.setId(tutor.getId());
		form.setNome(tutor.getNome());
		form.setCpf(tutor.getCpf());
		form.setEmail(tutor.getEmail());
		form.setTelefone(tutor.getTelefone());
		form.setDataNascimento(tutor.getDataNascimento());
		return form;
	}

}
