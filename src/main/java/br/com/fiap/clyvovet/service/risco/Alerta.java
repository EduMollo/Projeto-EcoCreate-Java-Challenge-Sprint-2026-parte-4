package br.com.fiap.clyvovet.service.risco;

import java.time.LocalDate;

import br.com.fiap.clyvovet.model.enums.Prioridade;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Alerta {

	String titulo;
	String descricao;
	Prioridade prioridade;
	int pontos;
	LocalDate dataLimite;

}
