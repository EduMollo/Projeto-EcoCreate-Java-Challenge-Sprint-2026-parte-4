package br.com.fiap.clyvovet.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import lombok.Getter;

/**
 * Principal autenticado. Carrega o que as telas e as regras de acesso precisam
 * (nome, perfis, tutor vinculado) sem exigir nova consulta ao banco a cada requisição.
 */
@Getter
public class UsuarioAutenticado implements UserDetails {

	private final Long id;
	private final String username;
	private final String senha;
	private final String nomeExibicao;
	private final Long idTutor;
	private final boolean ativo;
	private final Set<PerfilEnum> perfis;
	private final Set<GrantedAuthority> authorities;

	public UsuarioAutenticado(Usuario usuario) {
		this.id = usuario.getId();
		this.username = usuario.getUsername();
		this.senha = usuario.getSenha();
		this.nomeExibicao = usuario.getNomeExibicao();
		this.idTutor = usuario.getIdTutor();
		this.ativo = usuario.isAtivo();
		this.perfis = usuario.getPerfis().stream()
				.map(Perfil::getDescricao)
				.collect(Collectors.toUnmodifiableSet());
		this.authorities = perfis.stream()
				.map(perfil -> new SimpleGrantedAuthority(perfil.name()))
				.collect(Collectors.toUnmodifiableSet());
	}

	public boolean isAdmin() {
		return perfis.contains(PerfilEnum.ADMIN);
	}

	public boolean isVeterinario() {
		return perfis.contains(PerfilEnum.VETERINARIO);
	}

	public boolean isTutor() {
		return perfis.contains(PerfilEnum.TUTOR);
	}

	public boolean isEquipeClinica() {
		return isAdmin() || isVeterinario();
	}

	public String getDescricaoPerfis() {
		return perfis.stream().map(PerfilEnum::getDescricao).sorted().collect(Collectors.joining(", "));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isEnabled() {
		return ativo;
	}

}
