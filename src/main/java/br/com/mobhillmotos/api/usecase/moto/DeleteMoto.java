package br.com.mobhillmotos.api.usecase.moto;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.port.MotoRepository;
import br.com.mobhillmotos.api.port.UploadImage;

public class DeleteMoto {

	private final MotoRepository motoRepository;
	private final UploadImage uploadImage;

	public DeleteMoto(MotoRepository motoRepository, UploadImage uploadImage) {
		this.motoRepository = motoRepository;
		this.uploadImage = uploadImage;
	}

	public void execute(Long id) {
		Moto moto = motoRepository.getById(id);
		motoRepository.delete(id);

		if (moto.getImages() != null) {
			moto.getImages().forEach(image -> ImageCleanup.deleteQuietly(uploadImage, image.getUrl()));
		}
	}

}
