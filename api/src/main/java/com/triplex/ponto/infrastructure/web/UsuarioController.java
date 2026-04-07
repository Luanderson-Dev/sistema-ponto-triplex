package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.usecases.UsuarioUseCase;
import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.infrastructure.web.dto.CriarUsuarioRequest;
import com.triplex.ponto.infrastructure.web.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RH')")
public class UsuarioController {
    private final UsuarioUseCase usuarioUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody CriarUsuarioRequest request) {
        Usuario usuario = usuarioUseCase.criar(
                request.nome(),
                request.email(),
                request.senha(),
                request.role()
        );
        return UsuarioResponse.fromDomain(usuario);
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioUseCase.listarTodos().stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
    }
}
