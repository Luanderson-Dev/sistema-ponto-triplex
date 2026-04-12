package com.triplex.ponto.infrastructure.web.dto;

public record LeaderboardEntryResponse(
        int posicao,
        String nomeUsuario,
        String avatarUrl,
        long totalMinutos
) {}
