package com.triplex.ponto.application.application.ports;

import com.triplex.ponto.domain.RegistroPonto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistroPontoRepositoryPort {
    RegistroPonto salvar(RegistroPonto registroPonto);
    Optional<RegistroPonto> buscarPontoAbertoDoUsuario(Long usuarioId);
    List<RegistroPonto> listarPorUsuarioId(Long usuarioId);
    List<RegistroPonto> listarPorUsuarioIdEPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fim);
}
