package br.com.fiap.clyvovet.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.ContaForm;
import br.com.fiap.clyvovet.controller.form.UsuarioForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import br.com.fiap.clyvovet.repository.PerfilRepository;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import br.com.fiap.clyvovet.security.UsuarioLogadoProvider;

@Service
@Transactional
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PerfilRepository perfilRepository;
	private final TutorService tutorService;
	private final PasswordEncoder passwordEncoder;
	private final UsuarioLogadoProvider usuarioLogadoProvider;

	public UsuarioService(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository,
			TutorService tutorService, PasswordEncoder passwordEncoder, UsuarioLogadoProvider usuarioLogadoProvider) {
		this.usuarioRepository = usuarioRepository;
		this.perfilRepository = perfilRepository;
		this.tutorService = tutorService;
		this.passwordEncoder = passwordEncoder;
		this.usuarioLogadoProvider = usuarioLogadoProvider;
	}

	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return usuarioRepository.findAllByOrderByNomeExibicao();
	}

	/** Cadastro feito pelo administrador: qualquer perfil, tutor vinculado quando for o caso. */
	public Usuario cadastrar(UsuarioForm form) {
		Tutor tutor = form.getPerfil() == PerfilEnum.TUTOR ? tutorService.buscar(form.getIdTutor()) : null;
		return criar(form, form.getNomeExibicao(), form.getPerfil(), tutor);
	}

	/** Núcleo da criação de conta, compartilhado com o auto-cadastro de tutores. */
	public Usuario criar(ContaForm conta, String nomeExibicao, PerfilEnum perfil, Tutor tutor) {
		Perfil perfilCadastrado = perfilRepository.findByDescricao(perfil)
				.orElseThrow(() -> new RegraDeNegocioException("Perfil não cadastrado: " + perfil));

		return usuarioRepository.save(Usuario.builder()
				.username(conta.getUsername().trim().toLowerCase())
				.senha(passwordEncoder.encode(conta.getSenha()))
				.nomeExibicao(nomeExibicao.trim())
				.dataCriacao(LocalDate.now())
				.tutor(tutor)
				.perfis(Set.of(perfilCadastrado))
				.build());
	}

	public void alternarAtivo(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
		if (usuario.getId().equals(usuarioLogadoProvider.exigir().getId())) {
			throw new RegraDeNegocioException("Não é possível desativar o próprio usuário.");
		}
		usuario.setAtivo(!usuario.isAtivo());
	}

	@Transactional(readOnly = true)
	public long contar() {
		return usuarioRepository.count();
	}

}
