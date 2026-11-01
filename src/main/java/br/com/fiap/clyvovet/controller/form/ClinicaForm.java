package br.com.fiap.clyvovet.controller.form;

import br.com.fiap.clyvovet.validation.ClinicaUnica;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ClinicaUnica
public class ClinicaForm {

	private Long id;

	@NotBlank(message = "O nome é obrigatório.")
	@Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres.")
	private String nome;

	@NotBlank(message = "O CNPJ é obrigatório.")
	@Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos, sem pontuação.")
	private String cnpj;

	@NotBlank(message = "O telefone é obrigatório.")
	@Pattern(regexp = "\\d{10,11}", message = "O telefone deve ter 10 ou 11 dígitos, sem pontuação.")
	private String telefone;

	@NotBlank(message = "O e-mail é obrigatório.")
	@Email(message = "Informe um e-mail válido.")
	@Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
	private String email;

	@NotBlank(message = "O endereço é obrigatório.")
	@Size(max = 300, message = "O endereço deve ter no máximo 300 caracteres.")
	private String endereco;

	@NotBlank(message = "A cidade é obrigatória.")
	@Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
	private String cidade;

	@NotBlank(message = "O estado é obrigatório.")
	@Pattern(regexp = "[A-Z]{2}", message = "Informe a sigla do estado com 2 letras maiúsculas (ex.: SP).")
	private String estado;

}
