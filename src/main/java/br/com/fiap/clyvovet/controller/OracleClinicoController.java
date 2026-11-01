package br.com.fiap.clyvovet.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.clyvovet.controller.form.LeituraSensorForm;
import br.com.fiap.clyvovet.service.OracleClinicoService;
import jakarta.validation.Valid;

@Controller
@Profile("oracle")
@RequestMapping("/relatorios/oracle")
public class OracleClinicoController {
    private final OracleClinicoService clinico;
    public OracleClinicoController(OracleClinicoService clinico) { this.clinico = clinico; }

    @GetMapping
    public String pagina(Model model) {
        if (!model.containsAttribute("leituraSensorForm")) model.addAttribute("leituraSensorForm", new LeituraSensorForm());
        model.addAttribute("petsSensores", clinico.pets());
        model.addAttribute("auditorias", clinico.auditoria());
        return "relatorio/sensores";
    }

    @PostMapping("/leituras")
    public String registrar(@Valid @ModelAttribute LeituraSensorForm leituraSensorForm, BindingResult resultado,
            Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) return pagina(model);
        try {
            long id = clinico.registrarLeitura(leituraSensorForm);
            redirect.addFlashAttribute("mensagemSucesso", "Leitura " + id + " registrada com sucesso.");
            return "redirect:/relatorios/oracle";
        } catch (DataAccessException erro) {
            model.addAttribute("erro", "Não foi possível registrar. Confira o pet e os limites da leitura.");
            return pagina(model);
        }
    }

    @GetMapping("/risco")
    public String risco(@RequestParam Long petId, Model model) {
        if (petId <= 0) model.addAttribute("erro", "Selecione um pet válido.");
        else try {
            model.addAttribute("scoreSensor", clinico.calcularRisco(petId));
            model.addAttribute("petSelecionado", petId);
        } catch (DataAccessException erro) {
            model.addAttribute("erro", "Score indisponível. Confira se o pet é cão ou gato e possui leituras.");
        }
        return pagina(model);
    }
}
