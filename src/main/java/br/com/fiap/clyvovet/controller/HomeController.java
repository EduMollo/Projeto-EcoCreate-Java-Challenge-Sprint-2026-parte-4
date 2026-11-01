package br.com.fiap.clyvovet.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.fiap.clyvovet.security.UsuarioAutenticado;
import br.com.fiap.clyvovet.service.ClinicaService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.TutorService;
import br.com.fiap.clyvovet.service.UsuarioService;

/**
 * Página inicial: cada perfil cai em um painel diferente.
 */
@Controller
public class HomeController {

	private final ConsultaService consultaService;
	private final PetService petService;
	private final TutorService tutorService;
	private final ClinicaService clinicaService;
	private final UsuarioService usuarioService;

	public HomeController(ConsultaService consultaService, PetService petService, TutorService tutorService,
			ClinicaService clinicaService, UsuarioService usuarioService) {
		this.consultaService = consultaService;
		this.petService = petService;
		this.tutorService = tutorService;
		this.clinicaService = clinicaService;
		this.usuarioService = usuarioService;
	}

	@GetMapping({ "/", "/home" })
	public String inicio(@AuthenticationPrincipal UsuarioAutenticado usuario, Model model) {
		if (!usuario.isEquipeClinica()) {
			return "redirect:/meus-pets";
		}

		model.addAttribute("agendaDoDia", consultaService.agendaDoDia());
		model.addAttribute("naoRealizadas", consultaService.agendadasNaoRealizadas());

		if (usuario.isAdmin()) {
			model.addAttribute("totalPets", petService.contarAtivos());
			model.addAttribute("totalTutores", tutorService.contar());
			model.addAttribute("totalClinicas", clinicaService.contar());
			model.addAttribute("totalUsuarios", usuarioService.contar());
			return "home/admin";
		}
		return "home/veterinario";
	}

}
