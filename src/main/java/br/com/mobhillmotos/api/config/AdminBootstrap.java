package br.com.mobhillmotos.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.com.mobhillmotos.api.usecase.user.CreateInitialAdmin;

/** Cria o primeiro ADMIN no startup, já que só um ADMIN consegue criar usuários. */
@Component
public class AdminBootstrap implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

	private final CreateInitialAdmin createInitialAdmin;
	private final String name;
	private final String login;
	private final String password;

	public AdminBootstrap(CreateInitialAdmin createInitialAdmin,
			@Value("${app.security.admin.name}") String name,
			@Value("${app.security.admin.login}") String login,
			@Value("${app.security.admin.password}") String password) {
		this.createInitialAdmin = createInitialAdmin;
		this.name = name;
		this.login = login;
		this.password = password;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (createInitialAdmin.execute(name, login, password)) {
			log.info("Usuário ADMIN inicial criado. Login: {}", login);
		}
	}
}
