package br.com.mobhillmotos.api.port;

import java.util.List;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;

public interface MotoRepository {

	Moto save(Moto moto);

	/**
	 * @throws br.com.mobhillmotos.api.domain.exception.MotoNotFoundException se não existir
	 */
	Moto getById(Long motoId);

	/** Filtros nulos são ignorados. */
	List<Moto> listMoto(Boolean active, MotoType type);

	void delete(Long motoId);
}
