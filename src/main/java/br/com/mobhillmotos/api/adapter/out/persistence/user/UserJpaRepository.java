package br.com.mobhillmotos.api.adapter.out.persistence.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpa, Long> {

	Optional<UserJpa> findByLogin(String login);

	boolean existsByLogin(String login);

}
