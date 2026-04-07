package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.PontoUseCase;
import com.triplex.ponto.domain.RegistroPonto;
import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.infrastructure.web.dto.RegistroPontoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/internal/ponto")
@RequiredArgsConstructor
public class BotPontoController {
    private final PontoUseCase pontoUseCase;
    private final UsuarioRepositoryPort usuarioRepository;

    @Value("${app.bot.api-key}")
    private String botApiKey;

    @PostMapping("/entrada")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroPontoResponse registrarEntrada(
            @RequestHeader("X-Bot-Api-Key") String apiKey,
            @RequestParam String discordId
    ) {
        validarApiKey(apiKey);
        Usuario usuario = buscarUsuarioPorDiscordId(discordId);
        RegistroPonto ponto = pontoUseCase.registrarEntrada(usuario.getId());
        return RegistroPontoResponse.fromDomain(ponto, usuario);
    }

    @PostMapping("/saida")
    public RegistroPontoResponse registrarSaida(
            @RequestHeader("X-Bot-Api-Key") String apiKey,
            @RequestParam String discordId
    ) {
        validarApiKey(apiKey);
        Usuario usuario = buscarUsuarioPorDiscordId(discordId);
        RegistroPonto ponto = pontoUseCase.registrarSaida(usuario.getId());
        return RegistroPontoResponse.fromDomain(ponto, usuario);
    }

    private void validarApiKey(String apiKey) {
        if (!botApiKey.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API key inválida");
        }
    }

    private Usuario buscarUsuarioPorDiscordId(String discordId) {
        return usuarioRepository.buscarPorDiscordId(discordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário com discordId " + discordId + " não encontrado"));
    }
}
