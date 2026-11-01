package br.com.fiap.clyvovet.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelamentoForm {

	@NotBlank(message = "Informe o motivo do cancelamento.")
	@Size(min = 5, max = 300, message = "O motivo deve ter entre 5 e 300 caracteres.")
	private String motivo;

}
