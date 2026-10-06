package br.com.mobhillmotos.api.adapter.in;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.domain.UserType;
import br.com.mobhillmotos.api.usecase.user.CreateSeller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Acesso restrito ao perfil ADMIN (ver SecurityConfig). */
@Tag(name = "Usuários")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final CreateSeller createSeller;

	public UserController(CreateSeller createSeller) {
		this.createSeller = createSeller;
	}

	@Schema(description = "Dados para cadastro de um vendedor")
	public record CreateSellerRequest(
			@Schema(description = "Nome do vendedor", example = "João da Silva", maxLength = 100)
			@NotBlank(message = "O nome deve ser informado")
			@Size(max = 100, message = "O nome não deve exceder 100 caracteres")
			String name,

			@Schema(description = "Login de acesso (único)", example = "joao.silva", maxLength = 50)
			@NotBlank(message = "O login deve ser informado")
			@Size(max = 50, message = "O login não deve exceder 50 caracteres")
			String login,

			@Schema(description = "Senha de acesso", example = "senha1234", minLength = 8, maxLength = 72, format = "password")
			@NotBlank(message = "A senha deve ser informada")
			@Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
			String password) {
	}

	/** A senha nunca é devolvida. */
	@Schema(description = "Usuário cadastrado")
	public record UserResponse(
			@Schema(description = "Identificador do usuário", example = "1") Long id,
			@Schema(description = "Nome do usuário", example = "João da Silva") String name,
			@Schema(description = "Login do usuário", example = "joao.silva") String login,
			@Schema(description = "Perfil do usuário") UserType userType,
			@Schema(description = "Indica se o usuário está ativo", example = "true") Boolean active) {

		static UserResponse fromDomain(User user) {
			return new UserResponse(user.getId(), user.getName(), user.getLogin(), user.getUserType(),
					user.getActive());
		}
	}

	@Operation(summary = "Cadastra um vendedor", description = "Restrito ao perfil ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Vendedor criado"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
			@ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
			@ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
			@ApiResponse(responseCode = "409", description = "Login já existe", content = @Content)
	})
	@PostMapping
	public ResponseEntity<UserResponse> createSeller(@Valid @RequestBody CreateSellerRequest request) {
		User user = createSeller.execute(request.name(), request.login(), request.password());

		return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user));
	}
}
