package br.com.fiap.clyvovet.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.fiap.clyvovet.repository.UsuarioRepository;

@Service
public class UsuarioUserDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepository.findByUsername(username)
				.map(UsuarioAutenticado::new)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
	}

}
