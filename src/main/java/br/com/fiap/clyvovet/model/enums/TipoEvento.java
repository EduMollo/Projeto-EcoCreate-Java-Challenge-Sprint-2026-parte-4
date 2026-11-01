package br.com.fiap.clyvovet.model.enums;

public enum TipoEvento {

	CONSULTA("Consulta"),
	VACINA("Vacina"),
	MEDICAMENTO("Medicamento"),
	RETORNO("Retorno"),
	CHECKUP("Check-up"),
	VERMIFUGACAO("Vermifugação"),
	OBSERVACAO("Observação");

	private final String descricao;

	TipoEvento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
