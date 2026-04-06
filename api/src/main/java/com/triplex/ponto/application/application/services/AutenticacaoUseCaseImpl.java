package com.triplex.ponto.application.application.services;

import com.triplex.ponto.application.application.ports.RefreshTokenRepositoryPort;
import com.triplex.ponto.application.application.ports.TokenPort;
import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.AutenticacaoUseCase;
import com.triplex.ponto.domain.RefreshToken;
import com.triplex.ponto.domain.RespostaLogin;
import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.domain.exception.CredenciaisInvalidasException;
import com.triplex.ponto.domain.exception.TokenInvalidoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AutenticacaoUseCaseImpl implements AutenticacaoUseCase {
    private final UsuarioRepositoryPort usuarioRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;
    private final long expiracaoRefreshTokenMs;

    public AutenticacaoUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            TokenPort tokenPort,
            PasswordEncoder passwordEncoder,
            com.triplex.ponto.infrastructure.config.JwtProperties jwtProperties
    ) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
        this.expiracaoRefreshTokenMs = jwtProperties.getExpiracaoRefreshToken();
    }

    @Override
    @Transactional
    public RespostaLogin login(String email, String senha) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

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

        String novoAccessToken = tokenPort.gerarAccessToken(usuario.getId(), usuario.getEmail(), usuario.getRole());
        String novoRefreshTokenValue = tokenPort.gerarRefreshToken();

        Instant agora = Instant.now();
        RefreshToken novoRefreshToken = new RefreshToken(
                novoRefreshTokenValue,
                usuario.getId(),
                agora,
                agora.plus(expiracaoRefreshTokenMs, ChronoUnit.MILLIS)
        );
        refreshTokenRepository.salvar(novoRefreshToken);

        return new RespostaLogin(novoAccessToken, novoRefreshTokenValue, usuario.getNome(), usuario.getEmail(), usuario.getRole().name());
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
