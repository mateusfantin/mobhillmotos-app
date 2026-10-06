package br.com.mobhillmotos.api.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Builder
@Getter
public class User {

	private Long id;
	
	private String name;
	
	private String login;
	
	private String password;
	
	private UserType userType;
	
	private Boolean active;
	
}
