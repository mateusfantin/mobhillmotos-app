package br.com.mobhillmotos.api.adapter.out.persistence.moto;

import java.util.ArrayList;
import java.util.List;

import br.com.mobhillmotos.api.domain.MotoType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "moto")
public class MotoJpa {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "moto_id")
	private Long id;

	@Column(length = 100)
	private String model;

	@Column(length = 500)
	private String description;

	@Column(name = "moto_year")
	private Integer year;

	private Integer kilometers;

	private Boolean active;

	@Column(length = 4)
	@Enumerated(EnumType.STRING)
	private MotoType type;

	@Builder.Default
	@Convert(converter = BadgesConverter.class)
	@Column(length = 300)
	private List<String> badges = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "moto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MotoUploadJpa> images = new ArrayList<>();

}
