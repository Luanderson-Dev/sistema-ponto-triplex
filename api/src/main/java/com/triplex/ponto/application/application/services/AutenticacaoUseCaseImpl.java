package com.triplex.ponto.application.application.services;

import com.triplex.ponto.application.application.ports.RefreshTokenRepositoryPort;
import com.triplex.ponto.application.application.ports.TokenPort;
import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.AutenticacaoUseCase;
import com.triplex.ponto.domain.*;
import com.triplex.ponto.domain.exception.TokenInvalidoException;
import com.triplex.ponto.infrastructure.security.DiscordOAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AutenticacaoUseCaseImpl implements AutenticacaoUseCase {
    private final UsuarioRepositoryPort usuarioRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenPort tokenPort;
    private final DiscordOAuthService discordOAuthService;
    private final long expiracaoRefreshTokenMs;

    public AutenticacaoUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            TokenPort tokenPort,
            DiscordOAuthService discordOAuthService,
            com.triplex.ponto.infrastructure.config.JwtProperties jwtProperties
    ) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenPort = tokenPort;
        this.discordOAuthService = discordOAuthService;
        this.expiracaoRefreshTokenMs = jwtProperties.getExpiracaoRefreshToken();
    }

    @Override
    @Transactional
    public RespostaLogin loginComDiscord(String code) {
        String discordAccessToken = discordOAuthService.trocarCodePorToken(code);
        var memberData = discordOAuthService.obterMembroDoServidor(discordAccessToken);
        DiscordOAuthService.DiscordUser discordUser = discordOAuthService.obterUsuario(discordAccessToken);
        Role discordRole = discordOAuthService.determinarRole(memberData);

        Usuario usuario = usuarioRepository.buscarPorDiscordId(discordUser.id())
                .map(existente -> {
                    if (existente.getRole() != discordRole) {
                        existente.setRole(discordRole);
                        return usuarioRepository.salvar(existente);
                    }
                    return existente;
                })
                .orElseGet(() -> {
                    String email = discordUser.email() != null
                            ? discordUser.email()
                            : discordUser.id() + "@discord.user";
                    Usuario novo = new Usuario(null, discordUser.globalName(), email, null, discordRole, discordUser.id());
                    return usuarioRepository.salvar(novo);
                });

        return gerarRespostaLogin(usuario);
    }

    @Override
    @Transactional
    public RespostaLogin renovarToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.buscarPorToken(refreshTokenValue)
                .orElseThrow(TokenInvalidoException::new);

        if (!refreshToken.estaValido()) {
            refreshTokenRepository.revogarTodosPorUsuarioId(refreshToken.getUsuarioId());
            throw new TokenInvalidoException();
        }

        refreshToken.revogar();
        refreshTokenRepository.salvar(refreshToken);

        Usuario usuario = usuarioRepository.buscarPorId(refreshToken.getUsuarioId())
                .orElseThrow(TokenInvalidoException::new);

        return gerarRespostaLogin(usuario);
    }

    private RespostaLogin gerarRespostaLogin(Usuario usuario) {
        String accessToken = tokenPort.gerarAccessToken(usuario.getId(), usuario.getEmail(), usuario.getRole());
        String refreshTokenValue = tokenPort.gerarRefreshToken();

        Instant agora = Instant.now();
        RefreshToken refreshToken = new RefreshToken(
                refreshTokenValue,
                usuario.getId(),
                agora,
                agora.plus(expiracaoRefreshTokenMs, ChronoUnit.MILLIS)
        );
        refreshTokenRepository.salvar(refreshToken);

        return new RespostaLogin(accessToken, refreshTokenValue, usuario.getNome(), usuario.getEmail(), usuario.getRole().name());
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.buscarPorToken(refreshTokenValue).ifPresent(token -> {
            token.revogar();
            refreshTokenRepository.salvar(token);
        });
    }
}
