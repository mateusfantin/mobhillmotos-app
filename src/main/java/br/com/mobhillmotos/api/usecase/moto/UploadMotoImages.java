package br.com.mobhillmotos.api.usecase.moto;

import java.util.List;

import br.com.mobhillmotos.api.port.ImageFile;
import br.com.mobhillmotos.api.port.UploadImage;

public class UploadMotoImages {

	private final UploadImage uploadImage;

	public UploadMotoImages(UploadImage uploadImage) {
		this.uploadImage = uploadImage;
	}

	public List<String> execute(List<ImageFile> images) {
		if (images == null || images.isEmpty()) {
			return List.of();
		}
		return uploadImage.uploadImages(images);
	}

}
