package br.com.fiap.clyvovet.controller.form;

import java.time.LocalDate;

import br.com.fiap.clyvovet.validation.TutorUnico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TutorUnico
public class TutorForm {

	private Long id;

	@NotBlank(message = "O nome é obrigatório.")
	@Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
	private String nome;

	@NotBlank(message = "O CPF é obrigatório.")
	@Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos, sem pontuação.")
	private String cpf;

	@NotBlank(message = "O e-mail é obrigatório.")
	@Email(message = "Informe um e-mail válido.")
	@Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
	private String email;

	@NotBlank(message = "O telefone é obrigatório.")
	@Pattern(regexp = "\\d{10,11}", message = "O telefone deve ter 10 ou 11 dígitos, sem pontuação.")
	private String telefone;

	@NotNull(message = "A data de nascimento é obrigatória.")
	@Past(message = "A data de nascimento deve estar no passado.")
	private LocalDate dataNascimento;

}
