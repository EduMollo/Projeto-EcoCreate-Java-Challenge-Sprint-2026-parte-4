package br.com.fiap.clyvovet.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "username", nullable = false, unique = true, length = 60)
	@ToString.Include
	private String username;

	@Column(name = "senha", nullable = false, length = 100)
	private String senha;

	@Column(name = "nome_exibicao", nullable = false, length = 100)
	private String nomeExibicao;

	@Column(name = "ativo", nullable = false)
	@Builder.Default
	private boolean ativo = true;

	@Column(name = "data_criacao", nullable = false)
	private LocalDate dataCriacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tutor")
	private Tutor tutor;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_usuario_perfil",
			joinColumns = @JoinColumn(name = "id_usuario"),
			inverseJoinColumns = @JoinColumn(name = "id_perfil"))
	@Builder.Default
	private Set<Perfil> perfis = new HashSet<>();

	public boolean possuiPerfil(PerfilEnum perfil) {
		return perfis.stream().anyMatch(p -> p.getDescricao() == perfil);
	}

	public Long getIdTutor() {
		return tutor == null ? null : tutor.getId();
	}

}
