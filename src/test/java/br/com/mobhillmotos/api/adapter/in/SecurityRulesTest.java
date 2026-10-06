package br.com.mobhillmotos.api.adapter.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.mobhillmotos.api.adapter.out.security.BCryptPasswordHasher;
import br.com.mobhillmotos.api.adapter.out.security.JwtTokenIssuer;
import br.com.mobhillmotos.api.config.JwtConfig;
import br.com.mobhillmotos.api.config.SecurityConfig;
import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.domain.UserType;
import br.com.mobhillmotos.api.domain.exception.InvalidCredentialsException;
import br.com.mobhillmotos.api.port.AuthToken;
import br.com.mobhillmotos.api.port.TokenIssuer;
import br.com.mobhillmotos.api.usecase.moto.CreateMoto;
import br.com.mobhillmotos.api.usecase.moto.DeleteMoto;
import br.com.mobhillmotos.api.usecase.moto.SearchMoto;
import br.com.mobhillmotos.api.usecase.moto.UpdateMoto;
import br.com.mobhillmotos.api.usecase.moto.UploadMotoImages;
import br.com.mobhillmotos.api.usecase.user.Authenticate;
import br.com.mobhillmotos.api.usecase.user.CreateSeller;

@WebMvcTest({ AuthController.class, UserController.class, MotoController.class })
@Import({ SecurityConfig.class, JwtConfig.class, JwtTokenIssuer.class, BCryptPasswordHasher.class,
		GlobalExceptionHandler.class })
@TestPropertySource(properties = {
		"app.security.jwt.secret=test-secret-with-at-least-32-characters!!",
		"app.security.jwt.expiration=7d",
		"app.storage.dir=target/test-uploads" })
class SecurityRulesTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	TokenIssuer tokenIssuer;

	@MockitoBean
	Authenticate authenticate;
	@MockitoBean
	CreateSeller createSeller;
	@MockitoBean
	CreateMoto createMoto;
	@MockitoBean
	SearchMoto searchMoto;
	@MockitoBean
	UpdateMoto updateMoto;
	@MockitoBean
	DeleteMoto deleteMoto;
	@MockitoBean
	UploadMotoImages uploadMotoImages;

	private String tokenFor(UserType type) {
		return tokenIssuer.issue(User.builder().id(1L).login("x").userType(type).build()).token();
	}

	private static final String SELLER_JSON = """
			{"name":"Maria","login":"maria","password":"senha1234"}""";

	@Test
	void loginIsPublicAndReturnsBearerToken() throws Exception {
		when(authenticate.execute(anyString(), anyString())).thenReturn(new AuthToken("abc", Instant.now()));

		mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"login\":\"admin\",\"password\":\"x\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("abc"))
				.andExpect(jsonPath("$.tokenType").value("Bearer"));
	}

	@Test
	void wrongCredentialsReturn401() throws Exception {
		when(authenticate.execute(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

		mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"login\":\"admin\",\"password\":\"x\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminCanCreateSeller() throws Exception {
		when(createSeller.execute(anyString(), anyString(), anyString())).thenReturn(
				User.builder().id(2L).name("Maria").login("maria").userType(UserType.SELLER).active(true).build());

		mockMvc.perform(post("/api/v1/users").header("Authorization", "Bearer " + tokenFor(UserType.ADMIN))
				.contentType(MediaType.APPLICATION_JSON).content(SELLER_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.userType").value("SELLER"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void sellerCannotCreateUsers() throws Exception {
		mockMvc.perform(post("/api/v1/users").header("Authorization", "Bearer " + tokenFor(UserType.SELLER))
				.contentType(MediaType.APPLICATION_JSON).content(SELLER_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	void anonymousCannotCreateUsers() throws Exception {
		mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(SELLER_JSON))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void tamperedTokenIsRejected() throws Exception {
		String token = tokenFor(UserType.ADMIN);
		String tampered = token.substring(0, token.length() - 2) + "xx";

		mockMvc.perform(post("/api/v1/users").header("Authorization", "Bearer " + tampered)
				.contentType(MediaType.APPLICATION_JSON).content(SELLER_JSON))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void anonymousCanListButOnlySeesActiveMotos() throws Exception {
		when(searchMoto.list(any(), any())).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/motos").param("active", "false")).andExpect(status().isOk());

		verify(searchMoto).list(Boolean.TRUE, null);
	}

	@Test
	void staffCanListInactiveMotos() throws Exception {
		when(searchMoto.list(any(), any())).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/motos").header("Authorization", "Bearer " + tokenFor(UserType.SELLER)))
				.andExpect(status().isOk());

		verify(searchMoto).list(isNull(), isNull());
	}

	@Test
	void anonymousCannotSeeInactiveMotoById() throws Exception {
		when(searchMoto.execute(5L)).thenReturn(Moto.builder().id(5L).model("CG").active(false).build());

		mockMvc.perform(get("/api/v1/motos/5")).andExpect(status().isNotFound());
		mockMvc.perform(get("/api/v1/motos/5").header("Authorization", "Bearer " + tokenFor(UserType.SELLER)))
				.andExpect(status().isOk());
	}

	@Test
	void writingMotosRequiresAuthentication() throws Exception {
		mockMvc.perform(post("/api/v1/motos").contentType(MediaType.APPLICATION_JSON)
				.content("{\"model\":\"CG\",\"year\":2024,\"type\":\"NEW\"}"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(delete("/api/v1/motos/5")).andExpect(status().isUnauthorized());
	}
}
