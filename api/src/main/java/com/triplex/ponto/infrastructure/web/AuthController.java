package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.usecases.AutenticacaoUseCase;
import com.triplex.ponto.domain.RespostaLogin;
import com.triplex.ponto.infrastructure.config.DiscordProperties;
import com.triplex.ponto.infrastructure.web.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AutenticacaoUseCase autenticacaoUseCase;
    private final DiscordProperties discordProperties;

    @Value("${app.security.jwt.expiracao-refresh-token}")
    private long expiracaoRefreshTokenMs;

    @Value("${app.security.cookie.seguro:true}")
    private boolean cookieSeguro;

    @GetMapping("/discord/url")
    public ResponseEntity<Map<String, String>> discordAuthUrl() {
        String url = UriComponentsBuilder.fromUriString("https://discord.com/oauth2/authorize")
                .queryParam("client_id", discordProperties.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", discordProperties.getRedirectUri())
                .queryParam("scope", "identify email guilds.members.read")
                .build()
                .toUriString();

        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/discord/token")
    public ResponseEntity<LoginResponse> discordCallback(@RequestParam("code") String code) {
        RespostaLogin resposta = autenticacaoUseCase.loginComDiscord(code);

        ResponseCookie cookie = criarRefreshTokenCookie(resposta.getRefreshToken());

        LoginResponse response = new LoginResponse(
                resposta.getAccessToken(),
                resposta.getNomeUsuario(),
                resposta.getEmail(),
                resposta.getRole()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        RespostaLogin resposta = autenticacaoUseCase.renovarToken(refreshToken);

        ResponseCookie cookie = criarRefreshTokenCookie(resposta.getRefreshToken());

        LoginResponse response = new LoginResponse(
                resposta.getAccessToken(),
                resposta.getNomeUsuario(),
                resposta.getEmail(),
                resposta.getRole()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null) {
            autenticacaoUseCase.logout(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSeguro)
                .path("/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie criarRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSeguro)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofMillis(expiracaoRefreshTokenMs))
                .build();
    }
}
