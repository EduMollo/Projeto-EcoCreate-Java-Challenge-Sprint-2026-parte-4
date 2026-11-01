package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.PetForm;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.enums.Especie;
import br.com.fiap.clyvovet.model.enums.Sexo;
import br.com.fiap.clyvovet.service.ClinicaService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.TutorService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pets")
public class PetController {

	private static final String FORMULARIO = "pet/formulario";

	private final PetService petService;
	private final ConsultaService consultaService;
	private final TutorService tutorService;
	private final ClinicaService clinicaService;

	public PetController(PetService petService, ConsultaService consultaService, TutorService tutorService,
			ClinicaService clinicaService) {
		this.petService = petService;
		this.consultaService = consultaService;
		this.tutorService = tutorService;
		this.clinicaService = clinicaService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("pets", petService.listarAtivos());
		return "pet/lista";
	}

	@GetMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("petForm", new PetForm());
		return prepararFormulario(model);
	}

	@PostMapping
	public String salvar(@Valid PetForm petForm, BindingResult resultado, Model model, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararFormulario(model);
		}
		Pet pet = petService.cadastrar(petForm);
		redirect.addFlashAttribute("mensagemSucesso", "Pet cadastrado com sucesso.");
		return "redirect:/pets/" + pet.getId();
	}

	@GetMapping("/{id}")
	public String detalhes(@PathVariable Long id, Model model) {
		model.addAttribute("pet", petService.buscar(id));
		model.addAttribute("consultas", consultaService.listarDoPet(id));
		return "pet/detalhes";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("petForm", petService.carregarForm(id));
		return prepararFormulario(model);
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable Long id, @Valid PetForm petForm, BindingResult resultado, Model model,
			RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararFormulario(model);
		}
		petService.atualizar(id, petForm);
		redirect.addFlashAttribute("mensagemSucesso", "Pet atualizado com sucesso.");
		return "redirect:/pets/" + id;
	}

	@PostMapping("/{id}/alternar-ativo")
	public String alternarAtivo(@PathVariable Long id, RedirectAttributes redirect) {
		petService.alternarAtivo(id);
		redirect.addFlashAttribute("mensagemSucesso", "Situação do pet alterada.");
		return "redirect:/pets";
	}

	private String prepararFormulario(Model model) {
		model.addAttribute("tutores", tutorService.listarAtivos());
		model.addAttribute("clinicas", clinicaService.listarAtivas());
		model.addAttribute("especies", Especie.values());
		model.addAttribute("sexos", Sexo.values());
		return FORMULARIO;
	}

}
