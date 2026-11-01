package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.fiap.clyvovet.model.enums.Prioridade;

@Component
public class PetSeniorRegra implements RegraDeRisco {

	private static final int IDADE_SENIOR = 7;
	private static final int IDADE_IDOSO = 10;
	private static final long DIAS_SEM_CHECKUP = 180;
	private static final int PRAZO_DIAS = 30;

	@Override
	public Optional<Alerta> avaliar(ContextoSaudePet contexto) {
		int idade = contexto.idadeDoPet();
		if (idade < IDADE_SENIOR) {
			return Optional.empty();
		}
		boolean semCheckupRecente = contexto.diasDesdeUltimaConsulta()
				.map(dias -> dias > DIAS_SEM_CHECKUP)
				.orElse(true);

		return Optional.of(Alerta.builder()
				.titulo("Pet sênior")
				.descricao(semCheckupRecente
						? "Pet sênior (" + idade + " anos) sem check-up há mais de 6 meses. Exame semestral recomendado."
						: "Pet sênior (" + idade + " anos). Manter acompanhamento semestral.")
				.prioridade(semCheckupRecente ? Prioridade.ALTA : Prioridade.BAIXA)
				.pontos(idade >= IDADE_IDOSO ? 15 : 10)
				.dataLimite(contexto.hoje().plusDays(PRAZO_DIAS))
				.build());
	}

}
