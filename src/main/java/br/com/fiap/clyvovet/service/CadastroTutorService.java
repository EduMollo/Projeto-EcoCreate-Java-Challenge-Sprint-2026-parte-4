package br.com.fiap.clyvovet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.CadastroTutorForm;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;

/**
 * Auto-cadastro público: cria o tutor e a conta de acesso vinculada em uma única
 * transação. O perfil é sempre TUTOR — contas da equipe clínica só nascem pelo administrador.
 */
@Service
@Transactional
public class CadastroTutorService {

	private final TutorService tutorService;
	private final UsuarioService usuarioService;

	public CadastroTutorService(TutorService tutorService, UsuarioService usuarioService) {
		this.tutorService = tutorService;
		this.usuarioService = usuarioService;
	}

	public Usuario registrar(CadastroTutorForm form) {
		Tutor tutor = tutorService.cadastrar(form.getTutor());
		return usuarioService.criar(form.getConta(), tutor.getNome(), PerfilEnum.TUTOR, tutor);
	}

}
