package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.UsuarioForm;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;
import br.com.fiap.clyvovet.service.TutorService;
import br.com.fiap.clyvovet.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

	private static final String FORMULARIO = "usuario/formulario";
	private static final String REDIRECT_LISTA = "redirect:/usuarios";

	private final UsuarioService usuarioService;
	private final TutorService tutorService;

	public UsuarioController(UsuarioService usuarioService, TutorService tutorService) {
		this.usuarioService = usuarioService;
		this.tutorService = tutorService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("usuarios", usuarioService.listar());
		return "usuario/lista";
	}

	@GetMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("usuarioForm", new UsuarioForm());
		return prepararFormulario(model);
	}

	@PostMapping
	public String salvar(@Valid UsuarioForm usuarioForm, BindingResult resultado, Model model,
			RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararFormulario(model);
		}
		usuarioService.cadastrar(usuarioForm);
		redirect.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso.");
		return REDIRECT_LISTA;
	}

	@PostMapping("/{id}/alternar-ativo")
	public String alternarAtivo(@PathVariable Long id, RedirectAttributes redirect) {
		usuarioService.alternarAtivo(id);
		redirect.addFlashAttribute("mensagemSucesso", "Situação do usuário alterada.");
		return REDIRECT_LISTA;
	}

	private String prepararFormulario(Model model) {
		model.addAttribute("perfis", PerfilEnum.values());
		model.addAttribute("tutores", tutorService.listarAtivos());
		return FORMULARIO;
	}

}
