package br.com.fiap.clyvovet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Converte exceções de domínio em respostas de tela: recurso inexistente vira
 * página 404; violação de regra de negócio vira mensagem de erro na página de origem.
 * {@code AccessDeniedException} não é tratada aqui de propósito — fica com o Spring Security.
 */
@ControllerAdvice
public class TratadorDeExcecoes {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView recursoNaoEncontrado(RecursoNaoEncontradoException excecao) {
		ModelAndView view = new ModelAndView("error/404");
		view.addObject("mensagem", excecao.getMessage());
		return view;
	}

	@ExceptionHandler(RegraDeNegocioException.class)
	public String regraDeNegocio(RegraDeNegocioException excecao, HttpServletRequest request,
			RedirectAttributes redirect) {
		redirect.addFlashAttribute("mensagemErro", excecao.getMessage());
		String origem = request.getHeader("Referer");
		return "redirect:" + (origem != null ? origem : "/");
	}

}
