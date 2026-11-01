package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fiap.clyvovet.service.LinhaDoTempoService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.SaudePetService;

/**
 * Visões de saúde do pet: dashboard com score de risco e linha do tempo completa.
 * Acessível a qualquer perfil autenticado — a posse é verificada na camada de serviço.
 */
@Controller
@RequestMapping("/pets/{id}")
public class SaudePetController {

	private final SaudePetService saudePetService;
	private final PetService petService;
	private final LinhaDoTempoService linhaDoTempoService;

	public SaudePetController(SaudePetService saudePetService, PetService petService,
			LinhaDoTempoService linhaDoTempoService) {
		this.saudePetService = saudePetService;
		this.petService = petService;
		this.linhaDoTempoService = linhaDoTempoService;
	}

	@GetMapping("/dashboard")
	public String dashboard(@PathVariable Long id, Model model) {
		model.addAttribute("dashboard", saudePetService.gerarDashboard(id));
		return "pet/dashboard";
	}

	@GetMapping("/historico")
	public String historico(@PathVariable Long id, Model model) {
		model.addAttribute("pet", petService.buscar(id));
		model.addAttribute("eventos", linhaDoTempoService.listarDoPet(id));
		return "pet/historico";
	}

}
