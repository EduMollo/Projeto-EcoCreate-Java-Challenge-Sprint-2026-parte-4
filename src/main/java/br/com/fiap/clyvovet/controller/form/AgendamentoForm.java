package br.com.fiap.clyvovet.controller.form;

import java.time.LocalDate;

import br.com.fiap.clyvovet.model.enums.TipoConsulta;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgendamentoForm {

	@NotNull(message = "Selecione o pet.")
	private Long idPet;

	@NotNull(message = "Selecione a clínica.")
	private Long idClinica;

	@NotNull(message = "A data da consulta é obrigatória.")
	@FutureOrPresent(message = "Não é possível agendar uma consulta em data passada.")
	private LocalDate dataConsulta;

	@NotNull(message = "Selecione o tipo da consulta.")
	private TipoConsulta tipoConsulta;

	@NotBlank(message = "O nome do veterinário é obrigatório.")
	@Size(min = 3, max = 100, message = "O nome do veterinário deve ter entre 3 e 100 caracteres.")
	private String veterinario;

	@Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
	private String observacoes;

}
