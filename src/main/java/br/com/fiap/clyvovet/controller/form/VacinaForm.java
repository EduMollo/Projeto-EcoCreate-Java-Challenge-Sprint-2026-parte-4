package br.com.fiap.clyvovet.controller.form;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacinaForm {

	@NotBlank(message = "O nome da vacina é obrigatório.")
	@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
	private String nome;

	@NotBlank(message = "O fabricante é obrigatório.")
	@Size(max = 100, message = "O fabricante deve ter no máximo 100 caracteres.")
	private String fabricante;

	@Size(max = 50, message = "O lote deve ter no máximo 50 caracteres.")
	private String lote;

	@NotNull(message = "A data de aplicação é obrigatória.")
	@PastOrPresent(message = "A data de aplicação não pode ser futura.")
	private LocalDate dataAplicacao;

	@NotNull(message = "A data de validade é obrigatória.")
	private LocalDate dataValidade;

	private LocalDate proximaDose;

	@AssertTrue(message = "A validade deve ser posterior à data de aplicação.")
	public boolean isValidadePosteriorAplicacao() {
		return dataAplicacao == null || dataValidade == null || dataValidade.isAfter(dataAplicacao);
	}

}
