package br.com.fiap.clyvovet.model.enums;

public enum Especie {

	CACHORRO("Cachorro"),
	GATO("Gato"),
	PASSARO("Pássaro"),
	ROEDOR("Roedor"),
	REPTIL("Réptil"),
	OUTRO("Outro");

	private final String descricao;

	Especie(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
