package br.com.mobhillmotos.api.usecase.user;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.domain.UserType;
import br.com.mobhillmotos.api.port.PasswordHasher;
import br.com.mobhillmotos.api.port.UsuarioRepository;

/** Garante a existência do primeiro ADMIN. Idempotente: não faz nada se o login já existir. */
public class CreateInitialAdmin {

	private final UsuarioRepository userRepository;
	private final PasswordHasher passwordHasher;

	public CreateInitialAdmin(UsuarioRepository userRepository, PasswordHasher passwordHasher) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
	}

	/** @return true se o ADMIN foi criado agora */
	public boolean execute(String name, String login, String password) {
		String normalizedLogin = Logins.normalize(login);

		if (userRepository.existsByLogin(normalizedLogin)) {
			return false;
		}

		userRepository.save(User.builder()
				.name(name)
				.login(normalizedLogin)
				.password(passwordHasher.hash(password))
				.userType(UserType.ADMIN)
				.active(Boolean.TRUE)
				.build());
		return true;
	}
}
