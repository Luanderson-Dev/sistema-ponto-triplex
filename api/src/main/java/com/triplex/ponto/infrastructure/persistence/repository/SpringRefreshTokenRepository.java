package com.triplex.ponto.infrastructure.persistence.repository;

import com.triplex.ponto.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revogado = true WHERE r.usuarioId = :usuarioId AND r.revogado = false")
    void revogarTodosPorUsuarioId(@Param("usuarioId") Long usuarioId);
}
