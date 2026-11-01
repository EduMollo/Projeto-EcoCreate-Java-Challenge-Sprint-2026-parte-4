package br.com.fiap.clyvovet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "tb_vacina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Vacina {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "nome", nullable = false, length = 100)
	@ToString.Include
	private String nome;

	@Column(name = "fabricante", nullable = false, length = 100)
	private String fabricante;

	@Column(name = "lote", length = 50)
	private String lote;

	@Column(name = "data_aplicacao", nullable = false)
	private LocalDate dataAplicacao;

	@Column(name = "data_validade", nullable = false)
	private LocalDate dataValidade;

	@Column(name = "proxima_dose")
	private LocalDate proximaDose;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pet", nullable = false)
	private Pet pet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_clinica")
	private Clinica clinica;

	@PrePersist
	void aoPersistir() {
		if (criadoEm == null) {
			criadoEm = LocalDateTime.now();
		}
	}

	public boolean isVencida(LocalDate hoje) {
		return dataValidade.isBefore(hoje);
	}

	public boolean isDoseAtrasada(LocalDate hoje) {
		return proximaDose != null && proximaDose.isBefore(hoje);
	}

}
