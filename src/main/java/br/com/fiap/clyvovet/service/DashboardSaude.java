package br.com.fiap.clyvovet.service;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.EventoSaude;
import br.com.fiap.clyvovet.model.Medicamento;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.service.risco.ResultadoRisco;
import lombok.Builder;
import lombok.Value;

/**
 * Modelo de visualização do dashboard de saúde: tudo o que a tela precisa, já carregado.
 */
@Value
@Builder
public class DashboardSaude {

	Pet pet;
	ResultadoRisco risco;
	LocalDate ultimaConsulta;
	Long diasDesdeUltimaConsulta;
	int totalConsultas;
	List<Consulta> proximasConsultas;
	List<Vacina> vacinas;
	List<Medicamento> medicamentosAtivos;
	List<EventoSaude> ultimosEventos;

}
