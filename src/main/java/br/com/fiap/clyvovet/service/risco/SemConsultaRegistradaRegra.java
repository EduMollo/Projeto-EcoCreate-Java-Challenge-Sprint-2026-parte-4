package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.model.enums.Prioridade;

@Component
public class SemConsultaRegistradaRegra implements RegraDeRisco {

	private static final int PONTOS = 40;

	@Override
	public Optional<Alerta> avaliar(ContextoSaudePet contexto) {
		if (contexto.dataUltimaConsultaConcluida().isPresent()) {
			return Optional.empty();
		}
		return Optional.of(Alerta.builder()
				.titulo("Sem consulta registrada")
				.descricao(contexto.pet().getNome() + " nunca teve uma consulta concluída. Agende uma avaliação inicial.")
				.prioridade(Prioridade.ALTA)
				.pontos(PONTOS)
				.dataLimite(contexto.hoje())
				.build());
	}

}
