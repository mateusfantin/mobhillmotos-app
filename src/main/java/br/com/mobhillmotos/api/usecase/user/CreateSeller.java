package br.com.mobhillmotos.api.usecase.user;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.domain.UserType;
import br.com.mobhillmotos.api.domain.exception.LoginAlreadyExistsException;
import br.com.mobhillmotos.api.port.PasswordHasher;
import br.com.mobhillmotos.api.port.UsuarioRepository;

/** Cria usuários SELLER. Quem pode chamar (somente ADMIN) é decidido na camada web. */
public class CreateSeller {

	private final UsuarioRepository userRepository;
	private final PasswordHasher passwordHasher;

	public CreateSeller(UsuarioRepository userRepository, PasswordHasher passwordHasher) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
	}

	public User execute(String name, String login, String password) {
		String normalizedLogin = Logins.normalize(login);

		if (userRepository.existsByLogin(normalizedLogin)) {
			throw new LoginAlreadyExistsException(normalizedLogin);
		}

		return userRepository.save(User.builder()
				.name(name)
				.login(normalizedLogin)
				.password(passwordHasher.hash(password))
				.userType(UserType.SELLER)
				.active(Boolean.TRUE)
				.build());
	}
}
