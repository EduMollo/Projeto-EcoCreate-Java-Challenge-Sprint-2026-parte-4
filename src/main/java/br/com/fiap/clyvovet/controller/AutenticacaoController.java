package br.com.fiap.clyvovet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AutenticacaoController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/acesso-negado")
	public String acessoNegado() {
		return "acesso-negado";
	}

}
