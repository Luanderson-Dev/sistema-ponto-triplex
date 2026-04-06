package com.triplex.ponto.infrastructure.web.dto;

public record LoginResponse(
        String accessToken,
        String nomeUsuario,
        String email,
        String role
) {}
