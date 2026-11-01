package br.com.fiap.clyvovet.model.enums;

public enum Sexo {

	MACHO("Macho"),
	FEMEA("Fêmea");

	private final String descricao;

	Sexo(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
