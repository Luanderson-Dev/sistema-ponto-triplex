package com.triplex.ponto.infrastructure.persistence.repository;

import com.triplex.ponto.infrastructure.persistence.entity.RegistroPontoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringRegistroPontoRepository extends JpaRepository<RegistroPontoEntity, Long> {
    Optional<RegistroPontoEntity> findByUsuarioIdAndHoraSaidaIsNull(Long usuarioId);
    List<RegistroPontoEntity> findAllByUsuarioId(Long usuarioId);
    List<RegistroPontoEntity> findAllByUsuarioIdAndHoraEntradaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fim);
}
