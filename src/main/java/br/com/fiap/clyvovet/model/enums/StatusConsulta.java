package br.com.fiap.clyvovet.model.enums;

public enum StatusConsulta {

	AGENDADA("Agendada", "primary"),
	EM_ANDAMENTO("Em andamento", "warning"),
	CONCLUIDA("Concluída", "success"),
	CANCELADA("Cancelada", "secondary");

	private final String descricao;
	private final String corBootstrap;

	StatusConsulta(String descricao, String corBootstrap) {
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
