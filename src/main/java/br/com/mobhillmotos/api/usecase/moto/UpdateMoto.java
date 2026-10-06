package br.com.mobhillmotos.api.usecase.moto;

import java.util.Set;
import java.util.stream.Collectors;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoUpload;
import br.com.mobhillmotos.api.port.MotoRepository;
import br.com.mobhillmotos.api.port.UploadImage;

public class UpdateMoto {

	private final MotoRepository motoRepository;
	private final UploadImage uploadImage;

	public UpdateMoto(MotoRepository motoRepository, UploadImage uploadImage) {
		this.motoRepository = motoRepository;
		this.uploadImage = uploadImage;
	}

	public Moto execute(Long id, MotoCommand command) {
		Moto previous = motoRepository.getById(id);
		Moto updated = motoRepository.save(command.toDomain(id));

		// Remove do storage as imagens que deixaram de ser usadas pela moto
		Set<String> kept = updated.getImages() == null ? Set.of()
				: updated.getImages().stream().map(MotoUpload::getUrl).collect(Collectors.toSet());
		if (previous.getImages() != null) {
			previous.getImages().stream()
					.map(MotoUpload::getUrl)
					.filter(url -> !kept.contains(url))
					.forEach(url -> ImageCleanup.deleteQuietly(uploadImage, url));
		}

		return updated;
	}

}
