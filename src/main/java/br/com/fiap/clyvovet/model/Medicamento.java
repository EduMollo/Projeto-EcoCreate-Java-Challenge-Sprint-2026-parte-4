package br.com.fiap.clyvovet.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Medicamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "nome", nullable = false, length = 100)
	@ToString.Include
	private String nome;

	@Column(name = "principio_ativo", length = 100)
	private String principioAtivo;

	@Column(name = "dosagem", nullable = false, length = 100)
	private String dosagem;

	@Column(name = "frequencia", nullable = false, length = 100)
	private String frequencia;

	@Column(name = "data_inicio", nullable = false)
	private LocalDate dataInicio;

	@Column(name = "data_fim")
	private LocalDate dataFim;

	@Column(name = "uso_continuo", nullable = false)
	@Builder.Default
	private boolean usoContinuo = false;

	@Column(name = "observacoes", length = 500)
	private String observacoes;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pet", nullable = false)
	private Pet pet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_consulta")
	private Consulta consulta;

	public boolean isAtivo(LocalDate hoje) {
		return usoContinuo || dataFim == null || !dataFim.isBefore(hoje);
	}

}
