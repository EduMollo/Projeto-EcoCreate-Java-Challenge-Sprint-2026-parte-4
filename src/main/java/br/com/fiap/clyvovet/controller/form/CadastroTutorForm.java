package br.com.fiap.clyvovet.controller.form;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

/**
 * Auto-cadastro na tela de login: dados do tutor + credenciais. Compõe os
 * formulários existentes para reaproveitar todas as validações (inclusive unicidade).
 */
@Getter
@Setter
public class CadastroTutorForm {

	@Valid
	private TutorForm tutor = new TutorForm();

	@Valid
	private ContaForm conta = new ContaForm();

}
