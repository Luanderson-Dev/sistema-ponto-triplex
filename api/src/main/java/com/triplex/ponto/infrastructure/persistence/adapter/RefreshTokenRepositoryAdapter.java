package com.triplex.ponto.infrastructure.persistence.adapter;

import com.triplex.ponto.application.application.ports.RefreshTokenRepositoryPort;
import com.triplex.ponto.domain.RefreshToken;
import com.triplex.ponto.infrastructure.persistence.entity.RefreshTokenEntity;
import com.triplex.ponto.infrastructure.persistence.mapper.RefreshTokenMapper;
import com.triplex.ponto.infrastructure.persistence.repository.SpringRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {
    private final SpringRefreshTokenRepository repository;
    private final RefreshTokenMapper mapper;

    @Override
    public RefreshToken salvar(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> buscarPorToken(String token) {
        return repository.findByToken(token).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void revogarTodosPorUsuarioId(Long usuarioId) {
        repository.revogarTodosPorUsuarioId(usuarioId);
    }
}
