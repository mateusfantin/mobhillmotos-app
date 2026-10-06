package br.com.mobhillmotos.api.domain.exception;

public class MotoNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MotoNotFoundException(Long id) {
		super("Moto não encontrada. Id: " + id);
	}
}
