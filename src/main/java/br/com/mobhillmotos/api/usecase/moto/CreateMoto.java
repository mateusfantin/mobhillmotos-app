package br.com.mobhillmotos.api.usecase.moto;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.port.MotoRepository;

public class CreateMoto {

	private final MotoRepository motoRepository;

	public CreateMoto(MotoRepository motoRepository) {
		this.motoRepository = motoRepository;
	}

	public Moto execute(MotoCommand command) {
		return motoRepository.save(command.toDomain(null));
	}

}
