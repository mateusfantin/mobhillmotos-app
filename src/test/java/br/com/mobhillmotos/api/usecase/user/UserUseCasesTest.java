package br.com.mobhillmotos.api.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.domain.UserType;
import br.com.mobhillmotos.api.domain.exception.InvalidCredentialsException;
import br.com.mobhillmotos.api.domain.exception.LoginAlreadyExistsException;
import br.com.mobhillmotos.api.port.AuthToken;
import br.com.mobhillmotos.api.port.PasswordHasher;
import br.com.mobhillmotos.api.port.TokenIssuer;
import br.com.mobhillmotos.api.port.UsuarioRepository;

class UserUseCasesTest {

	private FakeUserRepository repository;
	private final PasswordHasher hasher = new PasswordHasher() {
		public String hash(String raw) {
			return "hashed:" + raw;
		}

		public boolean matches(String raw, String hashed) {
			return hashed.equals("hashed:" + raw);
		}
	};
	private final TokenIssuer issuer = user -> new AuthToken("token-" + user.getLogin(), Instant.now());

	@BeforeEach
	void setUp() {
		repository = new FakeUserRepository();
	}

	@Test
	void createSellerAlwaysCreatesActiveSellerWithHashedPassword() {
		User user = new CreateSeller(repository, hasher).execute("João", "  Joao  ", "senha1234");

		assertEquals(UserType.SELLER, user.getUserType());
		assertEquals("joao", user.getLogin());
		assertEquals("hashed:senha1234", user.getPassword());
		assertTrue(user.getActive());
	}

	@Test
	void createSellerRejectsDuplicatedLoginIgnoringCase() {
		CreateSeller createSeller = new CreateSeller(repository, hasher);
		createSeller.execute("João", "joao", "senha1234");

		assertThrows(LoginAlreadyExistsException.class, () -> createSeller.execute("Outro", "JOAO", "senha1234"));
	}

	@Test
	void initialAdminIsCreatedOnlyOnce() {
		CreateInitialAdmin createInitialAdmin = new CreateInitialAdmin(repository, hasher);

		assertTrue(createInitialAdmin.execute("Admin", "admin", "admin12345"));
		assertFalse(createInitialAdmin.execute("Admin", "admin", "outra-senha"));
		assertEquals(UserType.ADMIN, repository.findByLogin("admin").orElseThrow().getUserType());
		assertEquals("hashed:admin12345", repository.findByLogin("admin").orElseThrow().getPassword());
	}

	@Test
	void authenticateReturnsTokenForValidCredentials() {
		new CreateSeller(repository, hasher).execute("João", "joao", "senha1234");

		AuthToken token = new Authenticate(repository, hasher, issuer).execute("JOAO", "senha1234");

		assertEquals("token-joao", token.token());
	}

	@Test
	void authenticateFailsForWrongPasswordUnknownLoginOrInactiveUser() {
		new CreateSeller(repository, hasher).execute("João", "joao", "senha1234");
		repository.save(User.builder().name("Off").login("off").password("hashed:senha1234")
				.userType(UserType.SELLER).active(false).build());
		Authenticate authenticate = new Authenticate(repository, hasher, issuer);

		assertThrows(InvalidCredentialsException.class, () -> authenticate.execute("joao", "errada"));
		assertThrows(InvalidCredentialsException.class, () -> authenticate.execute("ninguem", "senha1234"));
		assertThrows(InvalidCredentialsException.class, () -> authenticate.execute("off", "senha1234"));
		assertNotEquals(null, repository.findByLogin("off").orElseThrow().getId());
	}

	private static class FakeUserRepository implements UsuarioRepository {

		private final Map<Long, User> users = new HashMap<>();
		private long sequence = 0;

		public Optional<User> findById(Long id) {
			return Optional.ofNullable(users.get(id));
		}

		public Optional<User> findByLogin(String login) {
			return users.values().stream().filter(u -> u.getLogin().equals(login)).findFirst();
		}

		public boolean existsByLogin(String login) {
			return findByLogin(login).isPresent();
		}

		public User save(User user) {
			User saved = User.builder().id(++sequence).name(user.getName()).login(user.getLogin())
					.password(user.getPassword()).userType(user.getUserType()).active(user.getActive()).build();
			users.put(saved.getId(), saved);
			return saved;
		}

		public void delete(Long id) {
			users.remove(id);
		}
	}
}
