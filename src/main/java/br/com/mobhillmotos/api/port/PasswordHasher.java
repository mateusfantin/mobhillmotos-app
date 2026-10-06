package br.com.mobhillmotos.api.port;

public interface PasswordHasher {

	String hash(String rawPassword);

	boolean matches(String rawPassword, String hashedPassword);
}
