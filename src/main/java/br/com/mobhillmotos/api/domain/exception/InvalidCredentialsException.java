package br.com.mobhillmotos.api.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidCredentialsException() {
		super("Login ou senha inválidos");
	}
}
