package br.com.fiap.clyvovet.controller.form;

import br.com.fiap.clyvovet.validation.UsuarioUnico;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Credenciais de acesso. Base tanto do auto-cadastro de tutor quanto do cadastro
 * de usuários feito pelo administrador.
 */
@Getter
@Setter
@UsuarioUnico
public class ContaForm {

	@NotBlank(message = "O nome de usuário é obrigatório.")
	@Size(min = 3, max = 60, message = "O nome de usuário deve ter entre 3 e 60 caracteres.")
	@Pattern(regexp = "[a-zA-Z0-9._-]+", message = "Use apenas letras, números, ponto, traço ou sublinhado.")
	private String username;

	@NotBlank(message = "A senha é obrigatória.")
	@Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres.")
	private String senha;

	@NotBlank(message = "Confirme a senha.")
	private String confirmacaoSenha;

	@AssertTrue(message = "As senhas não conferem.")
	public boolean isSenhasConferem() {
		return senha == null || senha.equals(confirmacaoSenha);
	}

}
