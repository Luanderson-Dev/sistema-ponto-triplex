package com.triplex.ponto.infrastructure.web.dto;

import com.triplex.ponto.domain.RegistroPonto;
import com.triplex.ponto.domain.Usuario;

import java.time.LocalDateTime;

public record RegistroPontoResponse(
        Long id,
        Long usuarioId,
        String nomeUsuario,
        String email,
        LocalDateTime horaEntrada,
        LocalDateTime horaSaida,
        long minutosTrabalhados
) {
    public static RegistroPontoResponse fromDomain(RegistroPonto ponto, Usuario usuario) {
        return new RegistroPontoResponse(
                ponto.getId(),
                ponto.getUsuarioId(),
                usuario.getNome(),
                usuario.getEmail(),
                ponto.getHoraEntrada(),
                ponto.getHoraSaida(),
                ponto.calcularMinutosTrabalhados()
        );
    }
}
