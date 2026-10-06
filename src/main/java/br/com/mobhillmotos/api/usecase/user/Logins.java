package br.com.mobhillmotos.api.usecase.user;

final class Logins {

	private Logins() {
	}

	static String normalize(String login) {
		return login == null ? null : login.trim().toLowerCase();
	}
}
