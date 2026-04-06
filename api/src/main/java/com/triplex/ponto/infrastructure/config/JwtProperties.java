package com.triplex.ponto.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {
    private String segredo;
    private long expiracaoAccessToken;
    private long expiracaoRefreshToken;

    public String getSegredo() { return segredo; }
    public void setSegredo(String segredo) { this.segredo = segredo; }

    public long getExpiracaoAccessToken() { return expiracaoAccessToken; }
    public void setExpiracaoAccessToken(long expiracaoAccessToken) { this.expiracaoAccessToken = expiracaoAccessToken; }

    public long getExpiracaoRefreshToken() { return expiracaoRefreshToken; }
    public void setExpiracaoRefreshToken(long expiracaoRefreshToken) { this.expiracaoRefreshToken = expiracaoRefreshToken; }
}
