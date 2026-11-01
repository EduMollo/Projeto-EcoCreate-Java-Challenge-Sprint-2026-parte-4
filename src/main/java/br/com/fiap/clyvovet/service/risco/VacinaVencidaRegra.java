package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.model.enums.Prioridade;

@Component
public class VacinaVencidaRegra implements RegraDeRisco {

	private static final int PONTOS_POR_VACINA = 10;
	private static final int PONTOS_MAXIMOS = 30;
	private static final int PRAZO_DIAS = 15;

	@Override
	public Optional<Alerta> avaliar(ContextoSaudePet contexto) {
		long vencidas = contexto.totalVacinasVencidas();
		if (vencidas == 0) {
			return Optional.empty();
		}
		return Optional.of(Alerta.builder()
				.titulo("Vacina vencida")
				.descricao(vencidas + " vacina(s) com validade expirada. Reforço necessário.")
				.prioridade(Prioridade.URGENTE)
				.pontos((int) Math.min(vencidas * PONTOS_POR_VACINA, PONTOS_MAXIMOS))
				.dataLimite(contexto.hoje().plusDays(PRAZO_DIAS))
				.build());
	}

}
