package br.com.mobhillmotos.api.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MotoUpload {

	private Long id;

	private String url;

	private Integer position;

	private Boolean main;
}
