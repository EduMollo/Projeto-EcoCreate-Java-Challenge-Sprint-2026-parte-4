package br.com.fiap.clyvovet.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import br.com.fiap.clyvovet.model.enums.Especie;
import br.com.fiap.clyvovet.model.enums.Sexo;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "nome", nullable = false, length = 80)
	@ToString.Include
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name = "especie", nullable = false, length = 20)
	private Especie especie;

	@Column(name = "raca", nullable = false, length = 80)
	private String raca;

	@Column(name = "data_nascimento", nullable = false)
	private LocalDate dataNascimento;

	@Column(name = "peso_kg", precision = 6, scale = 2)
	private BigDecimal pesoKg;

	@Enumerated(EnumType.STRING)
	@Column(name = "sexo", length = 10)
	private Sexo sexo;

	@Column(name = "castrado")
	private Boolean castrado;

	@Column(name = "microchip", unique = true, length = 20)
	private String microchip;

	@Column(name = "observacoes", length = 500)
	private String observacoes;

	@Column(name = "ativo", nullable = false)
	@Builder.Default
	private boolean ativo = true;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tutor", nullable = false)
	private Tutor tutor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_clinica_principal")
	private Clinica clinicaPrincipal;

	public int getIdadeEmAnos() {
		return Period.between(dataNascimento, LocalDate.now()).getYears();
	}

	public boolean pertenceAoTutor(Long idTutor) {
		return idTutor != null && idTutor.equals(tutor.getId());
	}

}
