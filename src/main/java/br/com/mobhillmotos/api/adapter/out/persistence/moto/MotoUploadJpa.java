package br.com.mobhillmotos.api.adapter.out.persistence.moto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "moto_upload")
public class MotoUploadJpa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 1000)
	private String url;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moto_id")
	private MotoJpa moto;

	// "order" é palavra reservada do SQL
	@Column(name = "position")
	private Integer position;

	@Column(name = "is_main")
	private Boolean main;
}
