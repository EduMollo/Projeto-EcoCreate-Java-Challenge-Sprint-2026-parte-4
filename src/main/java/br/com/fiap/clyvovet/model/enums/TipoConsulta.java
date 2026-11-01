package br.com.fiap.clyvovet.model.enums;

public enum TipoConsulta {

	ROTINA("Rotina"),
	PREVENTIVA("Preventiva"),
	EMERGENCIA("Emergência"),
	RETORNO("Retorno"),
	VACINACAO("Vacinação"),
	EXAME("Exame"),
	CIRURGIA("Cirurgia");

	private final String descricao;

	TipoConsulta(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
