package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.model.enums.Prioridade;

@Component
public class ConsultaAtrasadaRegra implements RegraDeRisco {

	private static final long DIAS_MUITO_ATRASADA = 540;
	private static final long DIAS_ATRASADA = 365;
	private static final long DIAS_ATENCAO = 180;
	private static final int PRAZO_DIAS = 30;

	@Override
	public Optional<Alerta> avaliar(ContextoSaudePet contexto) {
		return contexto.diasDesdeUltimaConsulta()
				.filter(dias -> dias > DIAS_ATENCAO)
				.map(dias -> Alerta.builder()
						.titulo("Check-up atrasado")
						.descricao("Última consulta foi há " + dias + " dias. Check-up anual recomendado.")
						.prioridade(prioridadePara(dias))
						.pontos(pontosPara(dias))
						.dataLimite(contexto.hoje().plusDays(PRAZO_DIAS))
						.build());
	}

	private Prioridade prioridadePara(long dias) {
		if (dias > DIAS_MUITO_ATRASADA) {
			return Prioridade.URGENTE;
		}
		return dias > DIAS_ATRASADA ? Prioridade.ALTA : Prioridade.MEDIA;
	}

	private int pontosPara(long dias) {
		if (dias > DIAS_MUITO_ATRASADA) {
			return 35;
		}
		return dias > DIAS_ATRASADA ? 25 : 10;
	}

}
