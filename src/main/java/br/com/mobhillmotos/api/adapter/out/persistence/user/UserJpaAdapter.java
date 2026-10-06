package br.com.mobhillmotos.api.adapter.out.persistence.user;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.port.UsuarioRepository;

@Component
@Transactional
public class UserJpaAdapter implements UsuarioRepository {

	private final UserJpaRepository userJpaRepository;

	public UserJpaAdapter(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findById(Long userId) {
		return userJpaRepository.findById(userId).map(this::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByLogin(String login) {
		return userJpaRepository.findByLogin(login).map(this::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByLogin(String login) {
		return userJpaRepository.existsByLogin(login);
	}

	@Override
	public User save(User user) {
		return toDomain(userJpaRepository.save(toJpa(user)));
	}

	@Override
	public void delete(Long userId) {
		userJpaRepository.deleteById(userId);
	}

	private UserJpa toJpa(User user) {
		UserJpa jpa = new UserJpa();
		jpa.setId(user.getId());
		jpa.setName(user.getName());
		jpa.setLogin(user.getLogin());
		jpa.setPassword(user.getPassword());
		jpa.setUserType(user.getUserType());
		jpa.setActive(user.getActive());
		return jpa;
	}

	private User toDomain(UserJpa jpa) {
		return User.builder()
				.id(jpa.getId())
				.name(jpa.getName())
				.login(jpa.getLogin())
				.password(jpa.getPassword())
				.userType(jpa.getUserType())
				.active(jpa.getActive())
				.build();
	}
}
