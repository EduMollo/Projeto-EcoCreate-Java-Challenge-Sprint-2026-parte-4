package br.com.fiap.clyvovet.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtendimentoForm {

	@NotBlank(message = "O diagnóstico é obrigatório para concluir o atendimento.")
	@Size(min = 5, max = 1000, message = "O diagnóstico deve ter entre 5 e 1000 caracteres.")
	private String diagnostico;

	@Size(max = 1000, message = "A prescrição deve ter no máximo 1000 caracteres.")
	private String prescricao;

	@Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
	private String observacoes;

	@DecimalMin(value = "0.0", message = "O valor não pode ser negativo.")
	@Digits(integer = 8, fraction = 2, message = "Informe o valor com até 2 casas decimais.")
	private BigDecimal valor;

	private LocalDate dataRetorno;

}
