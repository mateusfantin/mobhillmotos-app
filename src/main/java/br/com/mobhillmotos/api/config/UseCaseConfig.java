package br.com.mobhillmotos.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.mobhillmotos.api.port.MotoRepository;
import br.com.mobhillmotos.api.port.PasswordHasher;
import br.com.mobhillmotos.api.port.TokenIssuer;
import br.com.mobhillmotos.api.port.UploadImage;
import br.com.mobhillmotos.api.port.UsuarioRepository;
import br.com.mobhillmotos.api.usecase.moto.CreateMoto;
import br.com.mobhillmotos.api.usecase.moto.DeleteMoto;
import br.com.mobhillmotos.api.usecase.moto.SearchMoto;
import br.com.mobhillmotos.api.usecase.moto.UpdateMoto;
import br.com.mobhillmotos.api.usecase.moto.UploadMotoImages;
import br.com.mobhillmotos.api.usecase.user.Authenticate;
import br.com.mobhillmotos.api.usecase.user.CreateInitialAdmin;
import br.com.mobhillmotos.api.usecase.user.CreateSeller;

/** Liga os casos de uso às portas, mantendo o pacote usecase livre de Spring. */
@Configuration
public class UseCaseConfig {

	@Bean
	public CreateMoto createMoto(MotoRepository motoRepository) {
		return new CreateMoto(motoRepository);
	}

	@Bean
	public UpdateMoto updateMoto(MotoRepository motoRepository, UploadImage uploadImage) {
		return new UpdateMoto(motoRepository, uploadImage);
	}

	@Bean
	public DeleteMoto deleteMoto(MotoRepository motoRepository, UploadImage uploadImage) {
		return new DeleteMoto(motoRepository, uploadImage);
	}

	@Bean
	public SearchMoto searchMoto(MotoRepository motoRepository) {
		return new SearchMoto(motoRepository);
	}

	@Bean
	public UploadMotoImages uploadMotoImages(UploadImage uploadImage) {
		return new UploadMotoImages(uploadImage);
	}

	@Bean
	public Authenticate authenticate(UsuarioRepository userRepository, PasswordHasher passwordHasher,
			TokenIssuer tokenIssuer) {
		return new Authenticate(userRepository, passwordHasher, tokenIssuer);
	}

	@Bean
	public CreateSeller createSeller(UsuarioRepository userRepository, PasswordHasher passwordHasher) {
		return new CreateSeller(userRepository, passwordHasher);
	}

	@Bean
	public CreateInitialAdmin createInitialAdmin(UsuarioRepository userRepository, PasswordHasher passwordHasher) {
		return new CreateInitialAdmin(userRepository, passwordHasher);
	}
}
