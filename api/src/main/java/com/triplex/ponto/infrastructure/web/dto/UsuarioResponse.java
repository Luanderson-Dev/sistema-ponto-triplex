package com.triplex.ponto.infrastructure.web.dto;

import com.triplex.ponto.domain.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String role
) {
    public static UsuarioResponse fromDomain(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}
