package br.com.fiap.clyvovet.model.enums;

public enum PerfilEnum {

	ADMIN("Administrador"),
	VETERINARIO("Veterinário"),
	TUTOR("Tutor");

	private final String descricao;

	PerfilEnum(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
