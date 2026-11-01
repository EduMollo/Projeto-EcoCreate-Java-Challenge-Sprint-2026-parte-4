package br.com.fiap.clyvovet.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

	public RecursoNaoEncontradoException(String recurso, Long id) {
		super(recurso + " com id " + id + " não encontrado(a).");
	}

}
