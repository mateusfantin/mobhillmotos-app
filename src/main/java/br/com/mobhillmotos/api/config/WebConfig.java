package br.com.mobhillmotos.api.config;

import java.nio.file.Path;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Serve as fotos salvas por {@code LocalImageStorage}. Os nomes são UUIDs, então podem ter cache longo. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String directory;
	private final String publicPath;

	public WebConfig(@Value("${app.storage.dir}") String directory,
			@Value("${app.storage.public-path:/uploads}") String publicPath) {
		this.directory = directory;
		this.publicPath = publicPath.endsWith("/") ? publicPath.substring(0, publicPath.length() - 1) : publicPath;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = Path.of(directory).toAbsolutePath().normalize().toUri().toString();

		registry.addResourceHandler(publicPath + "/**")
				.addResourceLocations(location.endsWith("/") ? location : location + "/")
				.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
	}
}
