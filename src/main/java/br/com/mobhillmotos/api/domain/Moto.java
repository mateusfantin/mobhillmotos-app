package br.com.mobhillmotos.api.domain;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@Builder
public class Moto {

	private Long id;

	private String model;

	private String description;

	private Integer year;

	private Integer kilometers;

	private Boolean active;

	private MotoType type;

	private List<String> badges;

	private List<MotoUpload> images;
}
