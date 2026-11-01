package br.com.fiap.clyvovet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.ClinicaForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import br.com.fiap.clyvovet.service.mapper.ClinicaMapper;

@Service
@Transactional
public class ClinicaService {

	private final ClinicaRepository clinicaRepository;
	private final ClinicaMapper clinicaMapper;

	public ClinicaService(ClinicaRepository clinicaRepository, ClinicaMapper clinicaMapper) {
		this.clinicaRepository = clinicaRepository;
		this.clinicaMapper = clinicaMapper;
	}

	@Transactional(readOnly = true)
	public List<Clinica> listar() {
		return clinicaRepository.findAllByOrderByNome();
	}

	@Transactional(readOnly = true)
	public List<Clinica> listarAtivas() {
		return clinicaRepository.findByAtivoTrueOrderByNome();
	}

	@Transactional(readOnly = true)
	public Clinica buscar(Long id) {
		return clinicaRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Clínica", id));
	}

	@Transactional(readOnly = true)
	public ClinicaForm carregarForm(Long id) {
		return clinicaMapper.paraForm(buscar(id));
	}

	public Clinica cadastrar(ClinicaForm form) {
		return clinicaRepository.save(clinicaMapper.paraEntidade(form));
	}

	public void atualizar(Long id, ClinicaForm form) {
		clinicaMapper.atualizar(buscar(id), form);
	}

	public void alternarAtivo(Long id) {
		Clinica clinica = buscar(id);
		clinica.setAtivo(!clinica.isAtivo());
	}

	@Transactional(readOnly = true)
	public long contar() {
		return clinicaRepository.count();
	}

}
