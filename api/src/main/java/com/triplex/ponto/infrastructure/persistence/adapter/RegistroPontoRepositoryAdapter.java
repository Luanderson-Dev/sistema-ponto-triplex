package com.triplex.ponto.infrastructure.persistence.adapter;

import com.triplex.ponto.application.application.ports.RegistroPontoRepositoryPort;
import com.triplex.ponto.domain.RegistroPonto;
import com.triplex.ponto.infrastructure.persistence.entity.RegistroPontoEntity;
import com.triplex.ponto.infrastructure.persistence.mapper.RegistroPontoMapper;
import com.triplex.ponto.infrastructure.persistence.repository.SpringRegistroPontoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistroPontoRepositoryAdapter implements RegistroPontoRepositoryPort {
    private final SpringRegistroPontoRepository repository;
    private final RegistroPontoMapper mapper;

    @Override
    public RegistroPonto salvar(RegistroPonto registroPonto) {
        RegistroPontoEntity entity = mapper.toEntity(registroPonto);
        RegistroPontoEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RegistroPonto> buscarPontoAbertoDoUsuario(Long usuarioId) {
        return repository.findByUsuarioIdAndHoraSaidaIsNull(usuarioId)
                .map(mapper::toDomain);
    }

    @Override
    public List<RegistroPonto> listarPorUsuarioId(Long usuarioId) {
        return mapper.toDomainList(repository.findAllByUsuarioId(usuarioId));
    }

    @Override
    public List<RegistroPonto> listarPorUsuarioIdEPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fim) {
        return mapper.toDomainList(repository.findAllByUsuarioIdAndHoraEntradaBetween(usuarioId, inicio, fim));
    }
}
