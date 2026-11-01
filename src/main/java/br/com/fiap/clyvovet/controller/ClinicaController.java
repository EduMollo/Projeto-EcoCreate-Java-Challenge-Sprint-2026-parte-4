package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.ClinicaForm;
import br.com.fiap.clyvovet.service.ClinicaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clinicas")
public class ClinicaController {

	private static final String FORMULARIO = "clinica/formulario";
	private static final String REDIRECT_LISTA = "redirect:/clinicas";

	private final ClinicaService clinicaService;

	public ClinicaController(ClinicaService clinicaService) {
		this.clinicaService = clinicaService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("clinicas", clinicaService.listar());
		return "clinica/lista";
	}

	@GetMapping("/nova")
	public String nova(Model model) {
		model.addAttribute("clinicaForm", new ClinicaForm());
		return FORMULARIO;
	}

	@PostMapping
	public String salvar(@Valid ClinicaForm clinicaForm, BindingResult resultado, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return FORMULARIO;
		}
		clinicaService.cadastrar(clinicaForm);
		redirect.addFlashAttribute("mensagemSucesso", "Clínica cadastrada com sucesso.");
		return REDIRECT_LISTA;
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("clinicaForm", clinicaService.carregarForm(id));
		return FORMULARIO;
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable Long id, @Valid ClinicaForm clinicaForm, BindingResult resultado,
			RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return FORMULARIO;
		}
		clinicaService.atualizar(id, clinicaForm);
		redirect.addFlashAttribute("mensagemSucesso", "Clínica atualizada com sucesso.");
		return REDIRECT_LISTA;
	}

	@PostMapping("/{id}/alternar-ativo")
	public String alternarAtivo(@PathVariable Long id, RedirectAttributes redirect) {
		clinicaService.alternarAtivo(id);
		redirect.addFlashAttribute("mensagemSucesso", "Situação da clínica alterada.");
		return REDIRECT_LISTA;
	}

}
