package br.com.mobhillmotos.api.usecase.moto;

import java.util.List;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;
import br.com.mobhillmotos.api.domain.MotoUpload;

/** Dados de entrada dos casos de uso de criação e atualização de moto. */
public record MotoCommand(
		String model,
		String description,
		Integer year,
		Integer kilometers,
		Boolean active,
		MotoType type,
		List<String> badges,
		List<Image> images) {

	public record Image(String url, Integer position, Boolean main) {
	}

	public Moto toDomain(Long id) {
		List<MotoUpload> uploads = images == null ? List.of()
				: images.stream()
						.map(i -> MotoUpload.builder().url(i.url()).position(i.position()).main(i.main()).build())
						.toList();

		return Moto.builder()
				.id(id)
				.model(model)
				.description(description)
				.year(year)
				.kilometers(kilometers)
				.active(active == null ? Boolean.TRUE : active)
				.type(type)
				.badges(badges == null ? List.of()
						: badges.stream().map(String::trim).filter(b -> !b.isEmpty()).distinct().toList())
				.images(uploads)
				.build();
	}
}
