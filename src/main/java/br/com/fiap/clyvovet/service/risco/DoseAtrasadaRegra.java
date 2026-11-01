package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.model.enums.Prioridade;

@Component
public class DoseAtrasadaRegra implements RegraDeRisco {

	private static final int PONTOS_POR_DOSE = 10;
	private static final int PONTOS_MAXIMOS = 20;
	private static final int PRAZO_DIAS = 30;

	@Override
	public Optional<Alerta> avaliar(ContextoSaudePet contexto) {
		long atrasadas = contexto.totalDosesAtrasadas();
		if (atrasadas == 0) {
			return Optional.empty();
		}
		return Optional.of(Alerta.builder()
				.titulo("Dose de reforço atrasada")
				.descricao(atrasadas + " vacina(s) com próxima dose já vencida.")
				.prioridade(Prioridade.MEDIA)
				.pontos((int) Math.min(atrasadas * PONTOS_POR_DOSE, PONTOS_MAXIMOS))
				.dataLimite(contexto.hoje().plusDays(PRAZO_DIAS))
				.build());
	}

}
