package br.com.fiap.clyvovet.controller.form;

import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Cadastro de usuário pelo administrador: credenciais + perfil escolhido livremente.
 */
@Getter
@Setter
public class UsuarioForm extends ContaForm {

	@NotBlank(message = "O nome de exibição é obrigatório.")
	@Size(max = 100, message = "O nome de exibição deve ter no máximo 100 caracteres.")
	private String nomeExibicao;

	@NotNull(message = "Selecione o perfil.")
	private PerfilEnum perfil;

	private Long idTutor;

	@AssertTrue(message = "Para o perfil Tutor é obrigatório vincular um tutor cadastrado.")
	public boolean isTutorVinculadoQuandoNecessario() {
		return perfil != PerfilEnum.TUTOR || idTutor != null;
	}

}
