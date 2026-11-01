package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fiap.clyvovet.service.PetService;

/**
 * Área do tutor: lista apenas os pets vinculados ao usuário autenticado.
 */
@Controller
@RequestMapping("/meus-pets")
public class MeusPetsController {

	private final PetService petService;

	public MeusPetsController(PetService petService) {
		this.petService = petService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("pets", petService.listarDoTutorLogado());
		return "pet/meus-pets";
	}

}
