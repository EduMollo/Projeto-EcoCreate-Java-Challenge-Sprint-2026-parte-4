package br.com.fiap.clyvovet.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.fiap.clyvovet.model.enums.Especie;
import br.com.fiap.clyvovet.model.enums.Sexo;
import br.com.fiap.clyvovet.validation.PetUnico;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PetUnico
public class PetForm {

	private Long id;

	@NotBlank(message = "O nome é obrigatório.")
	@Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
	private String nome;

	@NotNull(message = "A espécie é obrigatória.")
	private Especie especie;

	@NotBlank(message = "A raça é obrigatória.")
	@Size(max = 80, message = "A raça deve ter no máximo 80 caracteres.")
	private String raca;

	@NotNull(message = "A data de nascimento é obrigatória.")
	@Past(message = "A data de nascimento deve estar no passado.")
	private LocalDate dataNascimento;

	@DecimalMin(value = "0.1", message = "O peso deve ser maior que 0,1 kg.")
	@DecimalMax(value = "200.0", message = "O peso deve ser menor que 200 kg.")
	private BigDecimal pesoKg;

	private Sexo sexo;

	private Boolean castrado;

	@Size(max = 20, message = "O microchip deve ter no máximo 20 caracteres.")
	private String microchip;

	@Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
	private String observacoes;

	@NotNull(message = "Selecione o tutor.")
	private Long idTutor;

	private Long idClinicaPrincipal;

}
