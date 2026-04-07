package com.triplex.ponto.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.discord")
public class DiscordProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String guildId;
    private String rhRoleId;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public String getGuildId() { return guildId; }
    public void setGuildId(String guildId) { this.guildId = guildId; }

    public String getRhRoleId() { return rhRoleId; }
    public void setRhRoleId(String rhRoleId) { this.rhRoleId = rhRoleId; }
}
