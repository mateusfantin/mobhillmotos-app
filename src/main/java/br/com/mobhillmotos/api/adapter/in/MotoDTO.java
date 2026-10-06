package br.com.mobhillmotos.api.adapter.in;

import java.util.List;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;
import br.com.mobhillmotos.api.usecase.moto.MotoCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de uma moto")
public record MotoDTO(

		@Schema(description = "Identificador da moto (gerado pela API)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
		Long id,

		@Schema(description = "Modelo da moto", example = "Honda CB 500F", maxLength = 100)
		@NotBlank(message = "Modelo da moto deve ser informado")
		@Size(max = 100, message = "O modelo não deve exceder 100 caracteres")
		String model,

		@Schema(description = "Descrição da moto", example = "Única dona, revisões em dia", maxLength = 500)
		@Size(max = 500, message = "A descrição não deve exceder 500 caracteres")
		String description,

		@Schema(description = "Ano de fabricação", example = "2022")
		@NotNull(message = "O ano da moto deve ser informado")
		Integer year,

		@Schema(description = "Quilometragem rodada", example = "12000")
		Integer kilometers,

		@Schema(description = "Indica se a moto está ativa (disponível para exibição)", example = "true")
		Boolean active,

		@Schema(description = "Condição da moto (nova ou semi-nova)")
		@NotNull(message = "Informar se a moto é nova ou semi-nova")
		MotoType type,

		@Schema(description = "Destaques exibidos no card (ex.: Único dono, Revisada)", example = "[\"Único dono\", \"Revisada\"]")
		@Size(max = 6, message = "Informe no máximo 6 destaques")
		List<@NotBlank(message = "O destaque não pode ser vazio") @Size(max = 30, message = "Cada destaque deve ter no máximo 30 caracteres") String> badges,

		@Schema(description = "Imagens da moto")
		@Valid
		List<ImageDTO> images) {

	@Schema(description = "Imagem de uma moto")
	public record ImageDTO(

			@Schema(description = "Identificador da imagem (gerado pela API)", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
			Long id,

			@Schema(description = "URL da imagem, obtida no endpoint de upload", example = "https://storage.exemplo.com/motos/cb500f-1.jpg")
			@NotBlank(message = "A url da imagem deve ser informada") String url,

			@Schema(description = "Posição da imagem na galeria", example = "0")
			Integer position,

			@Schema(description = "Indica se é a imagem principal", example = "true")
			Boolean main) {
	}

	public MotoCommand toCommand() {
		List<MotoCommand.Image> imageCommands = images == null ? List.of()
				: images.stream().map(i -> new MotoCommand.Image(i.url(), i.position(), i.main())).toList();

		return new MotoCommand(model, description, year, kilometers, active, type, badges, imageCommands);
	}

	public static MotoDTO fromDomain(Moto moto) {
		List<ImageDTO> imageDtos = moto.getImages() == null ? List.of()
				: moto.getImages().stream()
						.map(i -> new ImageDTO(i.getId(), i.getUrl(), i.getPosition(), i.getMain()))
						.toList();

		return new MotoDTO(moto.getId(), moto.getModel(), moto.getDescription(), moto.getYear(),
				moto.getKilometers(), moto.getActive(), moto.getType(), moto.getBadges(), imageDtos);
	}
}
