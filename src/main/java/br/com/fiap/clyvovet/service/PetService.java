package br.com.fiap.clyvovet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.controller.form.PetForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.service.mapper.PetMapper;

@Service
@Transactional
public class PetService {

	private final PetRepository petRepository;
	private final TutorService tutorService;
	private final ClinicaService clinicaService;
	private final AcessoPetService acessoPetService;
	private final PetMapper petMapper;

	public PetService(PetRepository petRepository, TutorService tutorService, ClinicaService clinicaService,
			AcessoPetService acessoPetService, PetMapper petMapper) {
		this.petRepository = petRepository;
		this.tutorService = tutorService;
		this.clinicaService = clinicaService;
		this.acessoPetService = acessoPetService;
		this.petMapper = petMapper;
	}

	@Transactional(readOnly = true)
	public List<Pet> listarAtivos() {
		return petRepository.findByAtivoTrueOrderByNome();
	}

	@Transactional(readOnly = true)
	public List<Pet> listarDoTutorLogado() {
		return petRepository.findByTutorIdAndAtivoTrueOrderByNome(acessoPetService.idTutorLogado());
	}

	/**
	 * Busca o pet e garante que o usuário logado pode vê-lo. Todo acesso a um pet
	 * específico passa por aqui.
	 */
	@Transactional(readOnly = true)
	public Pet buscar(Long id) {
		Pet pet = petRepository.findDetalhadoById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pet", id));
		acessoPetService.garantirAcesso(pet);
		return pet;
	}

	@Transactional(readOnly = true)
	public PetForm carregarForm(Long id) {
		return petMapper.paraForm(buscar(id));
	}

	public Pet cadastrar(PetForm form) {
		Pet pet = petMapper.paraEntidade(form, tutorService.buscar(form.getIdTutor()), clinicaDoForm(form));
		return petRepository.save(pet);
	}

	public void atualizar(Long id, PetForm form) {
		petMapper.atualizar(buscar(id), form, tutorService.buscar(form.getIdTutor()), clinicaDoForm(form));
	}

	public void alternarAtivo(Long id) {
		Pet pet = buscar(id);
		pet.setAtivo(!pet.isAtivo());
	}

	@Transactional(readOnly = true)
	public long contarAtivos() {
		return petRepository.countByAtivoTrue();
	}

	private Clinica clinicaDoForm(PetForm form) {
		return form.getIdClinicaPrincipal() == null ? null : clinicaService.buscar(form.getIdClinicaPrincipal());
	}

}
