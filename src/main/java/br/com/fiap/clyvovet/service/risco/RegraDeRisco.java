package br.com.fiap.clyvovet.service.risco;

import java.util.Optional;

/**
 * Strategy: cada regra de risco é uma implementação independente, registrada
 * como bean e injetada em lista no {@link AvaliadorDeRisco}. Adicionar uma regra
 * nova é criar uma classe — nenhum código existente precisa mudar.
 */
public interface RegraDeRisco {

	Optional<Alerta> avaliar(ContextoSaudePet contexto);

}
