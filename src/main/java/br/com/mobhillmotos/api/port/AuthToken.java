package br.com.mobhillmotos.api.port;

import java.time.Instant;

public record AuthToken(String token, Instant expiresAt) {
}
