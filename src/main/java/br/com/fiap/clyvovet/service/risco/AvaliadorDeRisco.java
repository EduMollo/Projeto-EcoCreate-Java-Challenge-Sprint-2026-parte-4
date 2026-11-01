package br.com.fiap.clyvovet.service.risco;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class AvaliadorDeRisco {

	private static final int SCORE_MAXIMO = 100;

	private final List<RegraDeRisco> regras;

	public AvaliadorDeRisco(List<RegraDeRisco> regras) {
		this.regras = regras;
	}

	public ResultadoRisco avaliar(ContextoSaudePet contexto) {
		List<Alerta> alertas = regras.stream()
				.map(regra -> regra.avaliar(contexto))
				.flatMap(Optional::stream)
				.sorted(Comparator.comparing(Alerta::getPrioridade))
				.toList();

		int score = Math.min(SCORE_MAXIMO, alertas.stream().mapToInt(Alerta::getPontos).sum());

		return new ResultadoRisco(score, ClassificacaoRisco.doScore(score), alertas);
	}

}
