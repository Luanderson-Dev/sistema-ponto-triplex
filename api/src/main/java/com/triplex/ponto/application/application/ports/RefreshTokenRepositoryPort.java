package com.triplex.ponto.application.application.ports;

import com.triplex.ponto.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken salvar(RefreshToken refreshToken);
    Optional<RefreshToken> buscarPorToken(String token);
    void revogarTodosPorUsuarioId(Long usuarioId);
}
