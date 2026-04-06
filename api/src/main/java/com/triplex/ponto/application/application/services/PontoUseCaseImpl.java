package com.triplex.ponto.application.application.services;

import com.triplex.ponto.application.application.ports.RegistroPontoRepositoryPort;
import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.PontoUseCase;
import com.triplex.ponto.domain.RegistroPonto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PontoUseCaseImpl implements PontoUseCase {
    private final RegistroPontoRepositoryPort pontoRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    public PontoUseCaseImpl(
            RegistroPontoRepositoryPort pontoRepository,
            UsuarioRepositoryPort usuarioRepository
    ) {
        this.pontoRepository = pontoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public RegistroPonto registrarEntrada(Long usuarioId) {
        usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        pontoRepository.buscarPontoAbertoDoUsuario(usuarioId).ifPresent(ponto -> {
            throw new IllegalStateException("Já existe um ponto em aberto para este usuário.");
        });

        RegistroPonto novoPonto = new RegistroPonto(usuarioId, LocalDateTime.now());

        return pontoRepository.salvar(novoPonto);
    }

    @Override
    public RegistroPonto registrarSaida(Long usuarioId) {
        RegistroPonto pontoAberto = pontoRepository.buscarPontoAbertoDoUsuario(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Não existe um ponto em aberto para este usuário."));

        pontoAberto.registrarSaida(LocalDateTime.now());

        return pontoRepository.salvar(pontoAberto);
    }

    @Override
    public List<RegistroPonto> listarPorUsuarioId(Long usuarioId) {
        return pontoRepository.listarPorUsuarioId(usuarioId);
    }

    @Override
    public List<RegistroPonto> listarPorUsuarioIdEPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fim) {
        return pontoRepository.listarPorUsuarioIdEPeriodo(usuarioId, inicio, fim);
    }

    @Override
    public Optional<RegistroPonto> buscarPontoAberto(Long usuarioId) {
        return pontoRepository.buscarPontoAbertoDoUsuario(usuarioId);
    }
}
