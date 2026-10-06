package br.com.mobhillmotos.api.usecase.user;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.domain.exception.InvalidCredentialsException;
import br.com.mobhillmotos.api.port.AuthToken;
import br.com.mobhillmotos.api.port.PasswordHasher;
import br.com.mobhillmotos.api.port.TokenIssuer;
import br.com.mobhillmotos.api.port.UsuarioRepository;

public class Authenticate {

	private final UsuarioRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final TokenIssuer tokenIssuer;

	public Authenticate(UsuarioRepository userRepository, PasswordHasher passwordHasher, TokenIssuer tokenIssuer) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
		this.tokenIssuer = tokenIssuer;
	}

	/** Falha sempre com a mesma exceção, sem revelar se o login existe. */
	public AuthToken execute(String login, String password) {
		User user = userRepository.findByLogin(Logins.normalize(login))
				.filter(u -> Boolean.TRUE.equals(u.getActive()))
				.filter(u -> passwordHasher.matches(password, u.getPassword()))
				.orElseThrow(InvalidCredentialsException::new);

		return tokenIssuer.issue(user);
	}
}
