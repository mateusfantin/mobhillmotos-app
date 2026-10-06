package br.com.mobhillmotos.api.domain.exception;

public class LoginAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LoginAlreadyExistsException(String login) {
		super("Já existe um usuário com o login: " + login);
	}
}
