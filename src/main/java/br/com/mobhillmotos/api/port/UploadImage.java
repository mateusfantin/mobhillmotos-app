package br.com.mobhillmotos.api.port;

import java.util.List;

public interface UploadImage {

	/**
	 * @return URLs públicas, na mesma ordem dos arquivos recebidos
	 * @throws br.com.mobhillmotos.api.domain.exception.ImageUploadException se algum upload falhar
	 */
	List<String> uploadImages(List<ImageFile> images);

	void delete(String fileName);
}
