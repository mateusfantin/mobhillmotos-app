package br.com.mobhillmotos.api.adapter.out.persistence.moto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Guarda a lista de destaques em uma única coluna, separados por "|". */
@Converter
public class BadgesConverter implements AttributeConverter<List<String>, String> {

	private static final String SEPARATOR = "|";

	@Override
	public String convertToDatabaseColumn(List<String> badges) {
		if (badges == null || badges.isEmpty()) {
			return null;
		}
		return String.join(SEPARATOR, badges.stream().map(b -> b.replace(SEPARATOR, " ").trim()).toList());
	}

	@Override
	public List<String> convertToEntityAttribute(String value) {
		if (value == null || value.isBlank()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(Arrays.stream(value.split("\\|"))
				.map(String::trim)
				.filter(b -> !b.isEmpty())
				.toList());
	}
}
