package br.com.mobhillmotos.api.port;

import java.util.Optional;

import br.com.mobhillmotos.api.domain.User;

public interface UsuarioRepository {

	Optional<User> findById(Long userId);

	Optional<User> findByLogin(String login);

	boolean existsByLogin(String login);

	User save(User user);

	void delete(Long userId);
}
