package br.com.mobhillmotos.api.port;

import br.com.mobhillmotos.api.domain.User;

public interface TokenIssuer {

	AuthToken issue(User user);
}
