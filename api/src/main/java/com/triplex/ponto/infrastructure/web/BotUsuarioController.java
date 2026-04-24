package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.usecases.UsuarioUseCase;
import com.triplex.ponto.infrastructure.security.ApiKeyValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/usuarios")
@RequiredArgsConstructor
public class BotUsuarioController {
    private final UsuarioUseCase usuarioUseCase;
    private final ApiKeyValidatorService apiKeyValidatorService;

    @PostMapping("/user-avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarAvatar(
            @RequestHeader("X-Bot-Api-Key") String apiKey,
            @RequestParam String discordId,
            @RequestParam String avatarUrl
    ) {
        apiKeyValidatorService.validarApiKey(apiKey);
        usuarioUseCase.updateAvatarUrl(discordId, avatarUrl);
    }
}
