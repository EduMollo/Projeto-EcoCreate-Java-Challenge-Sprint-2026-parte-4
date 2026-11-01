package br.com.fiap.clyvovet.service.risco;

public enum ClassificacaoRisco {

	BAIXO("Baixo", "success"),
	MEDIO("Médio", "warning"),
	ALTO("Alto", "danger");

	private static final int LIMITE_ALTO = 70;
	private static final int LIMITE_MEDIO = 40;

	private final String descricao;
	private final String corBootstrap;

	ClassificacaoRisco(String descricao, String corBootstrap) {
		this.descricao = descricao;
		this.corBootstrap = corBootstrap;
	}

	public static ClassificacaoRisco doScore(int score) {
		if (score >= LIMITE_ALTO) {
			return ALTO;
		}
		if (score >= LIMITE_MEDIO) {
			return MEDIO;
		}
		return BAIXO;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getCorBootstrap() {
		return corBootstrap;
	}

}
