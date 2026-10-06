package br.com.mobhillmotos.api.adapter.in;

import java.time.Instant;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mobhillmotos.api.port.AuthToken;
import br.com.mobhillmotos.api.usecase.user.Authenticate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final Authenticate authenticate;

	public AuthController(Authenticate authenticate) {
		this.authenticate = authenticate;
	}

	@Schema(description = "Credenciais de acesso")
	public record LoginRequest(
			@Schema(description = "Login do usuário", example = "admin")
			@NotBlank(message = "O login deve ser informado") String login,

			@Schema(description = "Senha do usuário", example = "senha1234", format = "password")
			@NotBlank(message = "A senha deve ser informada") String password) {
	}

	@Schema(description = "Token de acesso emitido após autenticação")
	public record LoginResponse(
			@Schema(description = "Token JWT")
			String token,

			@Schema(description = "Tipo do token", example = "Bearer")
			String tokenType,

			@Schema(description = "Data/hora de expiração do token")
			Instant expiresAt) {
	}

	@Operation(summary = "Autentica um usuário", description = "Retorna um token JWT para ser usado no header Authorization.",
			security = {})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Autenticado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
			@ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
	})
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		AuthToken authToken = authenticate.execute(request.login(), request.password());

		return new LoginResponse(authToken.token(), "Bearer", authToken.expiresAt());
	}
}
