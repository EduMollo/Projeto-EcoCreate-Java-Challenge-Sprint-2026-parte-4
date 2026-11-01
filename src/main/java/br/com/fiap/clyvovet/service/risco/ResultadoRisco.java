package br.com.fiap.clyvovet.service.risco;

import java.util.List;

import lombok.Value;

@Value
public class ResultadoRisco {

	int score;
	ClassificacaoRisco classificacao;
	List<Alerta> alertas;

}
