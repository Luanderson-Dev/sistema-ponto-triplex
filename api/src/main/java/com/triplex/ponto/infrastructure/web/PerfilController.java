package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.usecases.UsuarioUseCase;
import com.triplex.ponto.domain.DadosToken;
import com.triplex.ponto.infrastructure.web.dto.AlterarSenhaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {
    private final UsuarioUseCase usuarioUseCase;

    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal DadosToken dados,
            @Valid @RequestBody AlterarSenhaRequest request
    ) {
        usuarioUseCase.alterarSenha(dados.usuarioId(), request.senhaAtual(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
