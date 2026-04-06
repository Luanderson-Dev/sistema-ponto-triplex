package com.triplex.ponto.application.application.usecases;

import com.triplex.ponto.domain.RegistroPonto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontoUseCase {
    RegistroPonto registrarEntrada(Long usuarioId);
    RegistroPonto registrarSaida(Long usuarioId);
    List<RegistroPonto> listarPorUsuarioId(Long usuarioId);
    List<RegistroPonto> listarPorUsuarioIdEPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fim);
    Optional<RegistroPonto> buscarPontoAberto(Long usuarioId);
}
