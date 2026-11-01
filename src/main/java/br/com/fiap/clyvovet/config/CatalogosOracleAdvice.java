package br.com.fiap.clyvovet.config;

import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import br.com.fiap.clyvovet.controller.ConsultaController;
import br.com.fiap.clyvovet.service.OracleClinicoService;

@ControllerAdvice(assignableTypes = ConsultaController.class)
@Profile("oracle")
public class CatalogosOracleAdvice {
    private final OracleClinicoService clinico;
    public CatalogosOracleAdvice(OracleClinicoService clinico) { this.clinico = clinico; }
    @ModelAttribute("veterinariosOracle")
    public List<String> veterinarios() { return clinico.veterinarios(); }
}
