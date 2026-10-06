package br.com.mobhillmotos.api.usecase.moto;

import java.util.List;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;
import br.com.mobhillmotos.api.port.MotoRepository;

public class SearchMoto {

	private final MotoRepository motoRepository;

	public SearchMoto(MotoRepository motoRepository) {
		this.motoRepository = motoRepository;
	}

	public List<Moto> list(Boolean active, MotoType type) {
		return motoRepository.listMoto(active, type);
	}

	public Moto execute(Long id) {
		return motoRepository.getById(id);
	}

}
