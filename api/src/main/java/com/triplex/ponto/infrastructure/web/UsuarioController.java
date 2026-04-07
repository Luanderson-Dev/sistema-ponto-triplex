package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.usecases.UsuarioUseCase;
import com.triplex.ponto.infrastructure.web.dto.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RH')")
public class UsuarioController {
    private final UsuarioUseCase usuarioUseCase;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioUseCase.listarTodos().stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
    }
}
