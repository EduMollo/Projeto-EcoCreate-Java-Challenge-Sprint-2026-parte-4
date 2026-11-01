package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_clinica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Clinica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "nome", nullable = false, length = 150)
	@ToString.Include
	private String nome;

	@Column(name = "cnpj", nullable = false, unique = true, length = 14)
	private String cnpj;

	@Column(name = "telefone", nullable = false, length = 11)
	private String telefone;

	@Column(name = "email", nullable = false, length = 150)
	private String email;

	@Column(name = "endereco", nullable = false, length = 300)
	private String endereco;

	@Column(name = "cidade", nullable = false, length = 100)
	private String cidade;

	@Column(name = "estado", nullable = false, length = 2)
	private String estado;

	@Column(name = "ativo", nullable = false)
	@Builder.Default
	private boolean ativo = true;

}
