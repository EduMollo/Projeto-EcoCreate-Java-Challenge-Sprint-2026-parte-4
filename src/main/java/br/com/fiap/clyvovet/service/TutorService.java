package br.com.fiap.clyvovet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.TutorForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.TutorRepository;
import br.com.fiap.clyvovet.service.mapper.TutorMapper;

@Service
@Transactional
public class TutorService {

	private final TutorRepository tutorRepository;
	private final TutorMapper tutorMapper;

	public TutorService(TutorRepository tutorRepository, TutorMapper tutorMapper) {
		this.tutorRepository = tutorRepository;
		this.tutorMapper = tutorMapper;
	}

	@Transactional(readOnly = true)
	public List<Tutor> listar() {
		return tutorRepository.findAllByOrderByNome();
	}

	@Transactional(readOnly = true)
	public List<Tutor> listarAtivos() {
		return tutorRepository.findByAtivoTrueOrderByNome();
	}

	@Transactional(readOnly = true)
	public Tutor buscar(Long id) {
		return tutorRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Tutor", id));
	}

	@Transactional(readOnly = true)
	public TutorForm carregarForm(Long id) {
		return tutorMapper.paraForm(buscar(id));
	}

	public Tutor cadastrar(TutorForm form) {
		return tutorRepository.save(tutorMapper.paraEntidade(form));
	}

	public void atualizar(Long id, TutorForm form) {
		tutorMapper.atualizar(buscar(id), form);
	}

	public void alternarAtivo(Long id) {
		Tutor tutor = buscar(id);
		tutor.setAtivo(!tutor.isAtivo());
	}

	@Transactional(readOnly = true)
	public long contar() {
		return tutorRepository.count();
	}

}
