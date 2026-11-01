package br.com.fiap.clyvovet.model;

import java.util.Objects;

import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Perfil de acesso. Vive dentro de um {@code Set} em {@link Usuario}, por isso
 * equals/hashCode usam a chave natural ({@code descricao}) e não o id, que é
 * nulo antes do persist.
 */
@Entity
@Table(name = "tb_perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "descricao", nullable = false, unique = true, length = 20)
	private PerfilEnum descricao;

	public String getAuthority() {
		return descricao.name();
	}

	@Override
	public boolean equals(Object outro) {
		if (this == outro) {
			return true;
		}
		if (!(outro instanceof Perfil)) {
			return false;
		}
		return descricao == ((Perfil) outro).descricao;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(descricao);
	}

	@Override
	public String toString() {
		return "Perfil(" + descricao + ")";
	}

}
