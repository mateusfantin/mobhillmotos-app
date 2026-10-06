package br.com.mobhillmotos.api.adapter.in;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;
import br.com.mobhillmotos.api.domain.exception.InvalidImageException;
import br.com.mobhillmotos.api.domain.exception.MotoNotFoundException;
import br.com.mobhillmotos.api.port.ImageFile;
import br.com.mobhillmotos.api.usecase.moto.CreateMoto;
import br.com.mobhillmotos.api.usecase.moto.DeleteMoto;
import br.com.mobhillmotos.api.usecase.moto.SearchMoto;
import br.com.mobhillmotos.api.usecase.moto.UpdateMoto;
import br.com.mobhillmotos.api.usecase.moto.UploadMotoImages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Motos")
@RestController
@RequestMapping("/api/v1/motos")
public class MotoController {

	private static final int MAX_FILES_PER_UPLOAD = 6;

	private final CreateMoto createMoto;
	private final SearchMoto searchMoto;
	private final DeleteMoto deleteMoto;
	private final UpdateMoto updateMoto;
	private final UploadMotoImages uploadMotoImages;

	public MotoController(
			CreateMoto createMoto,
			SearchMoto searchMoto,
			DeleteMoto deleteMoto,
			UpdateMoto updateMoto,
			UploadMotoImages uploadMotoImages) {

		this.createMoto = createMoto;
		this.searchMoto = searchMoto;
		this.deleteMoto = deleteMoto;
		this.updateMoto = updateMoto;
		this.uploadMotoImages = uploadMotoImages;
	}

	@Operation(summary = "Cadastra uma moto")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Moto criada"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
	})
	@PostMapping
	public ResponseEntity<MotoDTO> criar(@Valid @RequestBody MotoDTO dto) {
		Moto moto = createMoto.execute(dto.toCommand());

		return ResponseEntity.status(HttpStatus.CREATED).body(MotoDTO.fromDomain(moto));
	}

	@Operation(summary = "Busca uma moto pelo id", description = "Público. Sem autenticação, só encontra motos ativas.",
			security = {})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Moto encontrada"),
			@ApiResponse(responseCode = "404", description = "Moto não encontrada", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<MotoDTO> buscar(@Parameter(description = "Id da moto", example = "1") @PathVariable Long id,
			@Parameter(hidden = true) Authentication authentication) {

		Moto moto = searchMoto.execute(id);
		if (!isStaff(authentication) && !Boolean.TRUE.equals(moto.getActive())) {
			throw new MotoNotFoundException(id);
		}
		return ResponseEntity.ok(MotoDTO.fromDomain(moto));
	}

	@Operation(summary = "Lista motos",
			description = "Público. Sem autenticação, retorna apenas motos ativas e o filtro 'active' é ignorado.",
			security = {})
	@GetMapping
	public ResponseEntity<List<MotoDTO>> listar(
			@Parameter(description = "Filtra por motos ativas/inativas (apenas usuários autenticados)") @RequestParam(required = false) Boolean active,
			@Parameter(description = "Filtra pela condição da moto") @RequestParam(required = false) MotoType type,
			@Parameter(hidden = true) Authentication authentication) {

		Boolean activeFilter = isStaff(authentication) ? active : Boolean.TRUE;

		List<MotoDTO> response = searchMoto.list(activeFilter, type).stream()
				.map(MotoDTO::fromDomain)
				.toList();

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Atualiza uma moto")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Moto atualizada"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
			@ApiResponse(responseCode = "404", description = "Moto não encontrada", content = @Content)
	})
	@PutMapping("/{id}")
	public ResponseEntity<MotoDTO> atualizar(@Parameter(description = "Id da moto", example = "1") @PathVariable Long id, @Valid @RequestBody MotoDTO dto) {
		Moto moto = updateMoto.execute(id, dto.toCommand());

		return ResponseEntity.ok(MotoDTO.fromDomain(moto));
	}

	@Operation(summary = "Exclui uma moto")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Moto excluída"),
			@ApiResponse(responseCode = "404", description = "Moto não encontrada", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@Parameter(description = "Id da moto", example = "1") @PathVariable Long id) {
		deleteMoto.execute(id);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Envia imagens de motos", description = "Aceita JPG, PNG, WEBP ou AVIF. Retorna as URLs das imagens enviadas.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Imagens enviadas"),
			@ApiResponse(responseCode = "400", description = "Arquivo inválido", content = @Content),
			@ApiResponse(responseCode = "413", description = "Arquivo grande demais", content = @Content),
			@ApiResponse(responseCode = "502", description = "Falha ao salvar no storage", content = @Content)
	})
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<String> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
		if (files.size() > MAX_FILES_PER_UPLOAD) {
			throw new InvalidImageException("Envie no máximo " + MAX_FILES_PER_UPLOAD + " imagens por vez.");
		}
		List<ImageFile> images = files.stream().map(MotoController::toImageFile).toList();

		return uploadMotoImages.execute(images);
	}

	/** Visitantes (sem token) só enxergam motos ativas. */
	private static boolean isStaff(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken);
	}

	private static ImageFile toImageFile(MultipartFile file) {
		try {
			return new ImageFile(file.getOriginalFilename(), file.getContentType(), file.getSize(),
					file.getInputStream());
		} catch (IOException e) {
			throw new UncheckedIOException("Não foi possível ler o arquivo: " + file.getOriginalFilename(), e);
		}
	}

}
