package br.com.fiap.clyvovet.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.MedicamentoForm;
import br.com.fiap.clyvovet.controller.form.VacinaForm;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PrescricaoService;
import jakarta.validation.Valid;

/**
 * Registros clínicos vinculados a uma consulta: medicamentos prescritos e vacinas aplicadas.
 */
@Controller
@RequestMapping("/consultas/{idConsulta}")
public class PrescricaoController {

	private static final String FORM_MEDICAMENTO = "prescricao/medicamento";
	private static final String FORM_VACINA = "prescricao/vacina";

	private final PrescricaoService prescricaoService;
	private final ConsultaService consultaService;

	public PrescricaoController(PrescricaoService prescricaoService, ConsultaService consultaService) {
		this.prescricaoService = prescricaoService;
		this.consultaService = consultaService;
	}

	@GetMapping("/medicamentos/novo")
	public String novoMedicamento(@PathVariable Long idConsulta, Model model) {
		MedicamentoForm form = new MedicamentoForm();
		form.setDataInicio(LocalDate.now());
		model.addAttribute("medicamentoForm", form);
		return prepararFormulario(idConsulta, model, FORM_MEDICAMENTO);
	}

	@PostMapping("/medicamentos")
	public String prescreverMedicamento(@PathVariable Long idConsulta, @Valid MedicamentoForm medicamentoForm,
			BindingResult resultado, Model model, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararFormulario(idConsulta, model, FORM_MEDICAMENTO);
		}
		prescricaoService.prescreverMedicamento(idConsulta, medicamentoForm);
		redirect.addFlashAttribute("mensagemSucesso", "Medicamento prescrito e registrado na linha do tempo.");
		return redirecionarParaConsulta(idConsulta);
	}

	@GetMapping("/vacinas/nova")
	public String novaVacina(@PathVariable Long idConsulta, Model model) {
		VacinaForm form = new VacinaForm();
		form.setDataAplicacao(LocalDate.now());
		model.addAttribute("vacinaForm", form);
		return prepararFormulario(idConsulta, model, FORM_VACINA);
	}

	@PostMapping("/vacinas")
	public String registrarVacina(@PathVariable Long idConsulta, @Valid VacinaForm vacinaForm,
			BindingResult resultado, Model model, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return prepararFormulario(idConsulta, model, FORM_VACINA);
		}
		prescricaoService.registrarVacina(idConsulta, vacinaForm);
		redirect.addFlashAttribute("mensagemSucesso", "Vacina registrada na carteira e na linha do tempo.");
		return redirecionarParaConsulta(idConsulta);
	}

	private String prepararFormulario(Long idConsulta, Model model, String view) {
		model.addAttribute("consulta", consultaService.buscar(idConsulta));
		return view;
	}

	private String redirecionarParaConsulta(Long idConsulta) {
		return "redirect:/consultas/" + idConsulta;
	}

}
