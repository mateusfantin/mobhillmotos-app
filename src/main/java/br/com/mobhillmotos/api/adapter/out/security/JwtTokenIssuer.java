package br.com.mobhillmotos.api.adapter.out.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import br.com.mobhillmotos.api.domain.User;
import br.com.mobhillmotos.api.port.AuthToken;
import br.com.mobhillmotos.api.port.TokenIssuer;

@Component
public class JwtTokenIssuer implements TokenIssuer {

	/** Claim que carrega o perfil (ADMIN ou SELLER). Lida por {@code SecurityConfig}. */
	public static final String ROLE_CLAIM = "role";

	private static final String ISSUER = "mobhillmotos-api";

	private final JwtEncoder jwtEncoder;
	private final Duration expiration;

	public JwtTokenIssuer(JwtEncoder jwtEncoder, @Value("${app.security.jwt.expiration:7d}") Duration expiration) {
		this.jwtEncoder = jwtEncoder;
		this.expiration = expiration;
	}

	@Override
	public AuthToken issue(User user) {
		Instant now = Instant.now();
		Instant expiresAt = now.plus(expiration);

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(user.getLogin())
				.issuedAt(now)
				.expiresAt(expiresAt)
				.claim("userId", user.getId())
				.claim(ROLE_CLAIM, user.getUserType().name())
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

		return new AuthToken(token, expiresAt);
	}
}
