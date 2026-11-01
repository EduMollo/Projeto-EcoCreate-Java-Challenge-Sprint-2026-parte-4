package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fiap.clyvovet.controller.form.CadastroTutorForm;
import br.com.fiap.clyvovet.service.CadastroTutorService;
import jakarta.validation.Valid;

/**
 * Criação de conta a partir da tela de login (rota pública).
 */
@Controller
@RequestMapping("/cadastro")
public class CadastroController {

	private static final String FORMULARIO = "cadastro";

	private final CadastroTutorService cadastroTutorService;

	public CadastroController(CadastroTutorService cadastroTutorService) {
		this.cadastroTutorService = cadastroTutorService;
	}

	@GetMapping
	public String novo(Model model) {
		model.addAttribute("cadastroTutorForm", new CadastroTutorForm());
		return FORMULARIO;
	}

	@PostMapping
	public String registrar(@Valid CadastroTutorForm cadastroTutorForm, BindingResult resultado) {
		if (resultado.hasErrors()) {
			return FORMULARIO;
		}
		cadastroTutorService.registrar(cadastroTutorForm);
		return "redirect:/login?cadastro=true";
	}

}
