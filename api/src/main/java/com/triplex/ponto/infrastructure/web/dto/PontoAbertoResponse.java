package com.triplex.ponto.infrastructure.web.dto;

import java.time.LocalDateTime;

public record PontoAbertoResponse(
        boolean aberto,
        LocalDateTime horaEntrada
) {}
