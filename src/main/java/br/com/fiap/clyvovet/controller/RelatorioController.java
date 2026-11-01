package br.com.fiap.clyvovet.controller;
import java.time.LocalDate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import br.com.fiap.clyvovet.service.RelatorioBancoService;
@Controller @RequestMapping("/relatorios")
public class RelatorioController {
 private final ObjectProvider<RelatorioBancoService> relatorios;
 public RelatorioController(ObjectProvider<RelatorioBancoService> relatorios) { this.relatorios=relatorios; }
 @GetMapping public String pagina(Model model) { model.addAttribute("relatoriosDisponiveis",relatorios.getIfAvailable()!=null); model.addAttribute("sensoresDisponiveis",relatorios.getIfAvailable() instanceof br.com.fiap.clyvovet.service.RelatorioOracleService); model.addAttribute("ano",LocalDate.now().getYear()); return "relatorio/index"; }
 @GetMapping("/atendimentos.json") public ResponseEntity<String> exportar(@RequestParam(required=false) Long clinica) {
  if(clinica!=null && clinica<=0) return ResponseEntity.badRequest().body("Clinica invalida");
  var servico=relatorios.getIfAvailable(); if(servico==null) return ResponseEntity.status(503).body("Relatorio indisponivel neste ambiente");
  try { return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8").header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=atendimentos.json").header(HttpHeaders.CACHE_CONTROL,"no-store").body(servico.exportar(clinica)); }
  catch(DataAccessException erro) { return ResponseEntity.status(422).body("Nao foi possivel exportar os atendimentos. Confira a clinica e a configuracao do banco."); }
 }
 @GetMapping("/faturamento") public String faturamento(@RequestParam int ano, Model model) {
  pagina(model);
  if(ano<2000 || ano>2100) model.addAttribute("erro","Informe um ano entre 2000 e 2100.");
  else if(relatorios.getIfAvailable()==null) model.addAttribute("erro","Relatorio indisponivel neste ambiente.");
  else try { model.addAttribute("resultado",relatorios.getObject().faturamento(ano)); }
  catch(DataAccessException erro) { model.addAttribute("erro","Nao foi possivel gerar o relatorio. Confira a configuracao do banco."); }
  return "relatorio/index";
 }
}
