package br.com.fiap.clyvovet.controller.form;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicamentoForm {

	@NotBlank(message = "O nome do medicamento é obrigatório.")
	@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
	private String nome;

	@Size(max = 100, message = "O princípio ativo deve ter no máximo 100 caracteres.")
	private String principioAtivo;

	@NotBlank(message = "A dosagem é obrigatória.")
	@Size(max = 100, message = "A dosagem deve ter no máximo 100 caracteres.")
	private String dosagem;

	@NotBlank(message = "A frequência é obrigatória.")
	@Size(max = 100, message = "A frequência deve ter no máximo 100 caracteres.")
	private String frequencia;

	@NotNull(message = "A data de início é obrigatória.")
	private LocalDate dataInicio;

	private LocalDate dataFim;

	private boolean usoContinuo;

	@Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
	private String observacoes;

	@AssertTrue(message = "A data de fim deve ser posterior à data de início.")
	public boolean isPeriodoValido() {
		return dataInicio == null || dataFim == null || dataFim.isAfter(dataInicio);
	}

}
