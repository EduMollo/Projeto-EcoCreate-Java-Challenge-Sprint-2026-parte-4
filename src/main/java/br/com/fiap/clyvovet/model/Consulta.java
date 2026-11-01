package br.com.fiap.clyvovet.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Consulta veterinária. As transições de status são métodos da própria entidade
 * para que o ciclo de vida (agendada → em andamento → concluída / cancelada)
 * seja garantido em um único lugar.
 */
@Entity
@Table(name = "tb_consulta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Consulta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "data_consulta", nullable = false)
	@ToString.Include
	private LocalDate dataConsulta;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	@ToString.Include
	private StatusConsulta status;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_consulta", nullable = false, length = 30)
	private TipoConsulta tipoConsulta;

	@Column(name = "veterinario", nullable = false, length = 100)
	private String veterinario;

	@Column(name = "diagnostico", length = 1000)
	private String diagnostico;

	@Column(name = "prescricao", length = 1000)
	private String prescricao;

	@Column(name = "observacoes", length = 500)
	private String observacoes;

	@Column(name = "valor", precision = 10, scale = 2)
	private BigDecimal valor;

	@Column(name = "data_retorno")
	private LocalDate dataRetorno;

	@Column(name = "motivo_cancelamento", length = 300)
	private String motivoCancelamento;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pet", nullable = false)
	private Pet pet;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_clinica", nullable = false)
	private Clinica clinica;

	@PrePersist
	void aoPersistir() {
		if (criadoEm == null) {
			criadoEm = LocalDateTime.now();
		}
		if (status == null) {
			status = StatusConsulta.AGENDADA;
		}
	}

	public void iniciarAtendimento() {
		exigirStatus(StatusConsulta.AGENDADA, "iniciar o atendimento");
		status = StatusConsulta.EM_ANDAMENTO;
	}

	public void concluir(String diagnostico, String prescricao, String observacoes,
			BigDecimal valor, LocalDate dataRetorno) {
		exigirStatus(StatusConsulta.EM_ANDAMENTO, "concluir o atendimento");
		if (dataRetorno != null && !dataRetorno.isAfter(dataConsulta)) {
			throw new RegraDeNegocioException("A data de retorno deve ser posterior à data da consulta.");
		}
		this.diagnostico = diagnostico;
		this.prescricao = prescricao;
		this.observacoes = observacoes;
		this.valor = valor;
		this.dataRetorno = dataRetorno;
		this.status = StatusConsulta.CONCLUIDA;
	}

	public void cancelar(String motivo) {
		if (status == StatusConsulta.CONCLUIDA || status == StatusConsulta.CANCELADA) {
			throw new RegraDeNegocioException(
					"Não é possível cancelar uma consulta " + status.getDescricao().toLowerCase() + ".");
		}
		this.motivoCancelamento = motivo;
		this.status = StatusConsulta.CANCELADA;
	}

	public boolean isAgendada() {
		return status == StatusConsulta.AGENDADA;
	}

	public boolean isEmAndamento() {
		return status == StatusConsulta.EM_ANDAMENTO;
	}

	public boolean isConcluida() {
		return status == StatusConsulta.CONCLUIDA;
	}

	public boolean isCancelavel() {
		return status == StatusConsulta.AGENDADA || status == StatusConsulta.EM_ANDAMENTO;
	}

	public boolean possuiRetornoPrevisto() {
		return isConcluida() && dataRetorno != null;
	}

	private void exigirStatus(StatusConsulta esperado, String acao) {
		if (status != esperado) {
			throw new RegraDeNegocioException("Só é possível " + acao + " de uma consulta "
					+ esperado.getDescricao().toLowerCase() + ". Status atual: "
					+ status.getDescricao().toLowerCase() + ".");
		}
	}

}
