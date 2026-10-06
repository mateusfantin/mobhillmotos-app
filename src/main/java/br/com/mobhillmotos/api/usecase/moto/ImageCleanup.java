package br.com.mobhillmotos.api.usecase.moto;

import br.com.mobhillmotos.api.port.UploadImage;

/** Exclusão de imagens que não deve impedir a operação principal (a moto já foi salva/excluída). */
final class ImageCleanup {

	private ImageCleanup() {
	}

	static void deleteQuietly(UploadImage uploadImage, String url) {
		try {
			uploadImage.delete(url);
		} catch (RuntimeException ignored) {
			// arquivo órfão no storage não afeta o catálogo
		}
	}
}
