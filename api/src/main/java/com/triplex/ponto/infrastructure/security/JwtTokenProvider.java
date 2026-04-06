package com.triplex.ponto.infrastructure.security;

import com.triplex.ponto.application.application.ports.TokenPort;
import com.triplex.ponto.domain.DadosToken;
import com.triplex.ponto.domain.Role;
import com.triplex.ponto.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider implements TokenPort {
    private final SecretKey chave;
    private final long expiracaoAccessToken;

    public JwtTokenProvider(JwtProperties properties) {
        this.chave = Keys.hmacShaKeyFor(properties.getSegredo().getBytes(StandardCharsets.UTF_8));
        this.expiracaoAccessToken = properties.getExpiracaoAccessToken();
    }

    @Override
    public String gerarAccessToken(Long usuarioId, String email, Role role) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoAccessToken);

        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("email", email)
                .claim("role", role.name())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    @Override
    public String gerarRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public DadosToken extrairDadosToken(String accessToken) {
        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        return new DadosToken(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                Role.valueOf(claims.get("role", String.class))
        );
    }

    @Override
    public boolean validarAccessToken(String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
