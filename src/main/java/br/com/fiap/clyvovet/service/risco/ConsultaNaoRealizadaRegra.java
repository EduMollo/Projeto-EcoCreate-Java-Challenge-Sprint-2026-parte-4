package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.model.enums.Prioridade;

@Component
public class ConsultaNaoRealizadaRegra implements RegraDeRisco {

	private static final int PONTOS = 15;
	private static final int PRAZO_DIAS = 7;

	@Override
	public Optional<Alerta> avaliar(ContextoSaudePet contexto) {
		long pendentes = contexto.totalConsultasNaoRealizadas();
		if (pendentes == 0) {
			return Optional.empty();
		}
		return Optional.of(Alerta.builder()
				.titulo("Consulta agendada não realizada")
				.descricao(pendentes + " consulta(s) agendada(s) com data já passada e sem atendimento. Reagende ou cancele.")
				.prioridade(Prioridade.ALTA)
				.pontos(PONTOS)
				.dataLimite(contexto.hoje().plusDays(PRAZO_DIAS))
				.build());
	}

}
