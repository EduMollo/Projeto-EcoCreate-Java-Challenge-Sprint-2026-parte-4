package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.AgendamentoForm;
import br.com.fiap.clyvovet.controller.form.AtendimentoForm;
import br.com.fiap.clyvovet.controller.form.CancelamentoForm;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.enums.StatusConsulta;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;
import br.com.fiap.clyvovet.service.ClinicaService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.PrescricaoService;
import jakarta.validation.Valid;

/**
 * Fluxo da consulta: agendar → iniciar atendimento → concluir / cancelar.
 */
@Controller
@RequestMapping("/consultas")
public class ConsultaController {

	private static final String AGENDAR = "consulta/agendar";
	private static final String DETALHES = "consulta/detalhes";
	private static final String ATENDER = "consulta/atender";

	private final ConsultaService consultaService;
	private final PrescricaoService prescricaoService;
	private final PetService petService;
	private final ClinicaService clinicaService;

	public ConsultaController(ConsultaService consultaService, PrescricaoService prescricaoService,
			PetService petService, ClinicaService clinicaService) {
		this.consultaService = consultaService;
		this.prescricaoService = prescricaoService;
		this.petService = petService;
		this.clinicaService = clinicaService;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) StatusConsulta status, Model model) {
		model.addAttribute("consultas", consultaService.listar(status));
		model.addAttribute("statusFiltro", status);
		model.addAttribute("listaStatus", StatusConsulta.values());
		return "consulta/lista";
	}

	@GetMapping("/nova")
	public String nova(@RequestParam(required = false) Long idPet, Model model) {
		AgendamentoForm form = new AgendamentoForm();
		form.setIdPet(idPet);
		model.addAttribute("agendamentoForm", form);
		return prepararAgendamento(model);
	}

	@PostMapping
	public String agendar(@Valid AgendamentoForm agendamentoForm, BindingResult resultado, Model model,
			RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararAgendamento(model);
		}
		Consulta consulta = consultaService.agendar(agendamentoForm);
		redirect.addFlashAttribute("mensagemSucesso", "Consulta agendada com sucesso.");
		return redirecionarParaDetalhes(consulta.getId());
	}

	@GetMapping("/{id}")
	public String detalhes(@PathVariable Long id, Model model) {
		model.addAttribute("cancelamentoForm", new CancelamentoForm());
		return prepararDetalhes(id, model);
	}

	@PostMapping("/{id}/iniciar")
	public String iniciar(@PathVariable Long id, RedirectAttributes redirect) {
		consultaService.iniciarAtendimento(id);
		redirect.addFlashAttribute("mensagemSucesso", "Atendimento iniciado.");
		return redirecionarParaDetalhes(id);
	}

	@GetMapping("/{id}/atender")
	public String atender(@PathVariable Long id, Model model) {
		model.addAttribute("consulta", consultaService.buscar(id));
		model.addAttribute("atendimentoForm", new AtendimentoForm());
		return ATENDER;
	}

	@PostMapping("/{id}/concluir")
	public String concluir(@PathVariable Long id, @Valid AtendimentoForm atendimentoForm, BindingResult resultado,
			Model model, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			model.addAttribute("consulta", consultaService.buscar(id));
			return ATENDER;
		}
		consultaService.concluir(id, atendimentoForm);
		redirect.addFlashAttribute("mensagemSucesso", "Atendimento concluído."
				+ (atendimentoForm.getDataRetorno() != null ? " Retorno agendado automaticamente." : ""));
		return redirecionarParaDetalhes(id);
	}

	@PostMapping("/{id}/cancelar")
	public String cancelar(@PathVariable Long id, @Valid CancelamentoForm cancelamentoForm,
			BindingResult resultado, Model model, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararDetalhes(id, model);
		}
		consultaService.cancelar(id, cancelamentoForm);
		redirect.addFlashAttribute("mensagemSucesso", "Consulta cancelada.");
		return redirecionarParaDetalhes(id);
	}

	private String prepararAgendamento(Model model) {
		model.addAttribute("pets", petService.listarAtivos());
		model.addAttribute("clinicas", clinicaService.listarAtivas());
		model.addAttribute("tipos", TipoConsulta.values());
		return AGENDAR;
	}

	private String prepararDetalhes(Long id, Model model) {
		model.addAttribute("consulta", consultaService.buscar(id));
		model.addAttribute("medicamentos", prescricaoService.medicamentosDaConsulta(id));
		return DETALHES;
	}

	private String redirecionarParaDetalhes(Long id) {
		return "redirect:/consultas/" + id;
	}

}
