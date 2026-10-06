package br.com.mobhillmotos.api.adapter.out.persistence.user;

import br.com.mobhillmotos.api.domain.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "app_user")
public class UserJpa {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100)
	private String name;

	@Column(length = 50, unique = true)
	private String login;

	@Column(length = 250)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(length = 6)
	private UserType userType;

	private Boolean active;
}
