package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.TutorForm;
import br.com.fiap.clyvovet.service.TutorService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/tutores")
public class TutorController {

	private static final String FORMULARIO = "tutor/formulario";
	private static final String REDIRECT_LISTA = "redirect:/tutores";

	private final TutorService tutorService;

	public TutorController(TutorService tutorService) {
		this.tutorService = tutorService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("tutores", tutorService.listar());
		return "tutor/lista";
	}

	@GetMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("tutorForm", new TutorForm());
		return FORMULARIO;
	}

	@PostMapping
	public String salvar(@Valid TutorForm tutorForm, BindingResult resultado, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return FORMULARIO;
		}
		tutorService.cadastrar(tutorForm);
		redirect.addFlashAttribute("mensagemSucesso", "Tutor cadastrado com sucesso.");
		return REDIRECT_LISTA;
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("tutorForm", tutorService.carregarForm(id));
		return FORMULARIO;
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable Long id, @Valid TutorForm tutorForm, BindingResult resultado,
			RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return FORMULARIO;
		}
		tutorService.atualizar(id, tutorForm);
		redirect.addFlashAttribute("mensagemSucesso", "Tutor atualizado com sucesso.");
		return REDIRECT_LISTA;
	}

	@PostMapping("/{id}/alternar-ativo")
	public String alternarAtivo(@PathVariable Long id, RedirectAttributes redirect) {
		tutorService.alternarAtivo(id);
		redirect.addFlashAttribute("mensagemSucesso", "Situação do tutor alterada.");
		return REDIRECT_LISTA;
	}

}
