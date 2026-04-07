package com.triplex.ponto.infrastructure.security;

import tools.jackson.databind.JsonNode;
import com.triplex.ponto.domain.exception.DiscordAuthException;
import com.triplex.ponto.infrastructure.config.DiscordProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class DiscordOAuthService {
    private static final String DISCORD_API = "https://discord.com/api/v10";
    private static final String TOKEN_URL = DISCORD_API + "/oauth2/token";

    private final DiscordProperties discordProperties;

    public String trocarCodePorToken(String code) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", discordProperties.getClientId());
        body.add("client_secret", discordProperties.getClientSecret());
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", discordProperties.getRedirectUri());

        JsonNode response = RestClient.create()
                .post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.has("access_token")) {
            throw new DiscordAuthException("Falha ao obter token do Discord.");
        }

        return response.get("access_token").asText();
    }

    public DiscordUser obterUsuario(String accessToken) {
        JsonNode response = discordApiGet("/users/@me", accessToken);

        String id = response.get("id").asText();
        String username = response.get("username").asText();
        String globalName = response.has("global_name") && !response.get("global_name").isNull()
                ? response.get("global_name").asText()
                : username;
        String email = response.has("email") && !response.get("email").isNull()
                ? response.get("email").asText()
                : null;

        return new DiscordUser(id, username, globalName, email);
    }

    public void verificarMembroDoServidor(String accessToken) {
        String guildId = discordProperties.getGuildId();
        try {
            discordApiGet("/users/@me/guilds/" + guildId + "/member", accessToken);
        } catch (Exception e) {
            throw new DiscordAuthException("Você não é membro do servidor autorizado.");
        }
    }

    private JsonNode discordApiGet(String path, String accessToken) {
        return RestClient.create()
                .get()
                .uri(DISCORD_API + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(JsonNode.class);
    }

    public record DiscordUser(String id, String username, String globalName, String email) {}
}
