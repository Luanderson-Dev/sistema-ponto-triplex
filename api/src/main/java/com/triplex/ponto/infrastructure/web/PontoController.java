package com.triplex.ponto.infrastructure.web;

import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.PontoUseCase;
import com.triplex.ponto.domain.DadosToken;
import com.triplex.ponto.domain.RegistroPonto;
import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.infrastructure.web.dto.PontoAbertoResponse;
import com.triplex.ponto.infrastructure.web.dto.RegistroPontoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontoController {
    private final PontoUseCase pontoUseCase;
    private final UsuarioRepositoryPort usuarioRepository;

    @PostMapping("/entrada")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroPontoResponse registrarEntrada(@AuthenticationPrincipal DadosToken dados) {
        RegistroPonto ponto = pontoUseCase.registrarEntrada(dados.usuarioId());
        Usuario usuario = buscarUsuario(dados.usuarioId());
        return RegistroPontoResponse.fromDomain(ponto, usuario);
    }

    @PostMapping("/saida")
    public RegistroPontoResponse registrarSaida(@AuthenticationPrincipal DadosToken dados) {
        RegistroPonto ponto = pontoUseCase.registrarSaida(dados.usuarioId());
        Usuario usuario = buscarUsuario(dados.usuarioId());
        return RegistroPontoResponse.fromDomain(ponto, usuario);
    }

    @GetMapping("/aberto")
    public PontoAbertoResponse verificarPontoAberto(@AuthenticationPrincipal DadosToken dados) {
        return pontoUseCase.buscarPontoAberto(dados.usuarioId())
                .map(p -> new PontoAbertoResponse(true, p.getHoraEntrada()))
                .orElse(new PontoAbertoResponse(false, null));
    }

    @GetMapping("/meus")
    public List<RegistroPontoResponse> listarMeusPontos(@AuthenticationPrincipal DadosToken dados) {
        Usuario usuario = buscarUsuario(dados.usuarioId());
        return pontoUseCase.listarPorUsuarioId(dados.usuarioId()).stream()
                .map(p -> RegistroPontoResponse.fromDomain(p, usuario))
                .toList();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('RH')")
    public List<RegistroPontoResponse> listarPontosAdmin(
            @RequestParam Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        Usuario usuario = buscarUsuario(usuarioId);
        return pontoUseCase.listarPorUsuarioIdEPeriodo(usuarioId, inicio, fim).stream()
                .map(p -> RegistroPontoResponse.fromDomain(p, usuario))
                .toList();
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }
}
