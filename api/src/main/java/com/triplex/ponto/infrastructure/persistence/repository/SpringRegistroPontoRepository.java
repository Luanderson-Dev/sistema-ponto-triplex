package com.triplex.ponto.infrastructure.persistence.repository;

import com.triplex.ponto.infrastructure.persistence.entity.RegistroPontoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringRegistroPontoRepository extends JpaRepository<RegistroPontoEntity, Long> {
    Optional<RegistroPontoEntity> findByUsuarioIdAndHoraSaidaIsNull(Long usuarioId);
    List<RegistroPontoEntity> findAllByUsuarioId(Long usuarioId);
    List<RegistroPontoEntity> findAllByUsuarioIdAndHoraEntradaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fim);

    @Query(value = """
            SELECT u.nome AS nomeUsuario, u.avatar_url AS avatarUrl,
                   COALESCE(SUM(EXTRACT(EPOCH FROM (rp.hora_saida - rp.hora_entrada)) / 60), 0) AS totalMinutos
            FROM usuarios u
            LEFT JOIN registros_ponto rp ON rp.usuario_id = u.id AND rp.hora_saida IS NOT NULL
            GROUP BY u.id, u.nome, u.avatar_url
            ORDER BY totalMinutos DESC
            """, nativeQuery = true)
    List<LeaderboardProjection> findLeaderboard();
}
