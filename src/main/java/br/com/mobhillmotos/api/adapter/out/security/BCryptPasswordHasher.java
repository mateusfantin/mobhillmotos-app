package br.com.mobhillmotos.api.adapter.out.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.mobhillmotos.api.port.PasswordHasher;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

	private final PasswordEncoder passwordEncoder;

	public BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String hash(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String hashedPassword) {
		return rawPassword != null && passwordEncoder.matches(rawPassword, hashedPassword);
	}
}
