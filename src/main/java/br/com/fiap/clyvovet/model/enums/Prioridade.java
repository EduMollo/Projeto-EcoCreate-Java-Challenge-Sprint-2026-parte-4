package br.com.fiap.clyvovet.model.enums;

public enum Prioridade {

	URGENTE("Urgente", "danger"),
	ALTA("Alta", "warning"),
	MEDIA("Média", "info"),
	BAIXA("Baixa", "secondary");

	private final String descricao;
	private final String corBootstrap;

	Prioridade(String descricao, String corBootstrap) {
		this.descricao = descricao;
		this.corBootstrap = corBootstrap;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getCorBootstrap() {
		return corBootstrap;
	}

}
