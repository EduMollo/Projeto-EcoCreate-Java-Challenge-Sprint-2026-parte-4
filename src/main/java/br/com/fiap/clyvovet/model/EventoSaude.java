package br.com.fiap.clyvovet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.fiap.clyvovet.model.enums.TipoEvento;
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
 * Linha do tempo do pet: cada passo relevante da jornada de saúde gera um evento.
 */
@Entity
@Table(name = "tb_evento_saude")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class EventoSaude {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_evento", nullable = false, length = 30)
	@ToString.Include
	private TipoEvento tipoEvento;

	@Column(name = "descricao", nullable = false, length = 500)
	private String descricao;

	@Column(name = "data_evento", nullable = false)
	@ToString.Include
	private LocalDate dataEvento;

	@Column(name = "data_proxima_acao")
	private LocalDate dataProximaAcao;

	@Column(name = "concluido", nullable = false)
	@Builder.Default
	private boolean concluido = false;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pet", nullable = false)
	private Pet pet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_consulta")
	private Consulta consulta;

	@PrePersist
	void aoPersistir() {
		if (criadoEm == null) {
			criadoEm = LocalDateTime.now();
		}
	}

	public boolean isPendente(LocalDate hoje) {
		return !concluido && dataProximaAcao != null && !dataProximaAcao.isAfter(hoje);
	}

}
