package com.triplex.ponto.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public class RegistroPonto {
    private Long id;
    private Long usuarioId;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;

    public RegistroPonto() {}

    public RegistroPonto(Long usuarioId, LocalDateTime horaEntrada) {
        this.usuarioId = usuarioId;
        this.horaEntrada = horaEntrada;
    }

    public void registrarSaida(LocalDateTime hora) {
        if (this.horaSaida != null) {
            throw new IllegalStateException("O ponto de saída já foi registado para este turno.");
        }
        if (hora.isBefore(this.horaEntrada)) {
            throw new IllegalArgumentException("A hora de saída não pode ser anterior à hora de entrada.");
        }
        this.horaSaida = hora;
    }

    public long calcularMinutosTrabalhados() {
        if (this.horaSaida == null) {
            return 0;
        }
        return Duration.between(this.horaEntrada, this.horaSaida).toMinutes();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalDateTime getHoraSaida() { return horaSaida; }
    public void setHoraSaida(LocalDateTime horaSaida) { this.horaSaida = horaSaida; }
}
