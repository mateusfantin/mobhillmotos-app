package br.com.mobhillmotos.api.adapter.out.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.mobhillmotos.api.domain.exception.ImageUploadException;
import br.com.mobhillmotos.api.domain.exception.InvalidImageException;
import br.com.mobhillmotos.api.port.ImageFile;
import br.com.mobhillmotos.api.port.UploadImage;

/**
 * Guarda as imagens em uma pasta do servidor (no Docker, um volume) e devolve URLs relativas
 * ("/uploads/arquivo.jpg"), servidas por {@link br.com.mobhillmotos.api.config.WebConfig}.
 */
@Component
public class LocalImageStorage implements UploadImage {

	/** Tipos aceitos e a extensão gravada. O nome original do arquivo nunca é usado no disco. */
	private static final Map<String, String> EXTENSIONS = Map.of(
			"image/jpeg", ".jpg",
			"image/png", ".png",
			"image/webp", ".webp",
			"image/avif", ".avif");

	private final Path directory;
	private final String publicPath;

	public LocalImageStorage(@Value("${app.storage.dir}") String directory,
			@Value("${app.storage.public-path:/uploads}") String publicPath) throws IOException {
		this.directory = Path.of(directory).toAbsolutePath().normalize();
		this.publicPath = publicPath.endsWith("/") ? publicPath.substring(0, publicPath.length() - 1) : publicPath;
		Files.createDirectories(this.directory);
	}

	@Override
	public List<String> uploadImages(List<ImageFile> images) {
		images.forEach(LocalImageStorage::validate);

		List<String> saved = new ArrayList<>();
		for (ImageFile image : images) {
			String fileName = UUID.randomUUID() + EXTENSIONS.get(image.contentType());

			try (InputStream content = image.content()) {
				Files.copy(content, directory.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException | RuntimeException e) {
				saved.forEach(this::deleteQuietly);
				deleteQuietly(fileName);
				throw new ImageUploadException("Falha ao salvar a imagem: " + image.fileName(), e);
			}
			saved.add(fileName);
		}

		return saved.stream().map(name -> publicPath + "/" + name).toList();
	}

	/** Aceita o nome do arquivo ou a URL devolvida no upload. URLs externas são ignoradas. */
	@Override
	public void delete(String fileNameOrUrl) {
		if (fileNameOrUrl == null || fileNameOrUrl.isBlank()) {
			return;
		}
		if (fileNameOrUrl.contains("/") && !fileNameOrUrl.startsWith(publicPath + "/")) {
			return;
		}

		String fileName = fileNameOrUrl.substring(fileNameOrUrl.lastIndexOf('/') + 1);
		Path file = directory.resolve(fileName).normalize();
		if (!file.getParent().equals(directory)) {
			return; // tentativa de sair da pasta (ex.: "../")
		}

		try {
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new ImageUploadException("Falha ao excluir a imagem: " + fileName, e);
		}
	}

	private void deleteQuietly(String fileName) {
		try {
			delete(fileName);
		} catch (RuntimeException ignored) {
			// limpeza best-effort
		}
	}

	private static void validate(ImageFile image) {
		if (image.size() <= 0) {
			throw new InvalidImageException("Arquivo vazio: " + image.fileName());
		}
		if (image.contentType() == null || !EXTENSIONS.containsKey(image.contentType())) {
			throw new InvalidImageException("Formato não suportado: " + image.fileName()
					+ ". Envie JPG, PNG, WEBP ou AVIF.");
		}
	}
}
