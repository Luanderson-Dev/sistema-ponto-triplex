package com.triplex.ponto.application.application.services;

import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.UsuarioUseCase;
import com.triplex.ponto.domain.Role;
import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.domain.exception.SenhaIncorretaException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioUseCaseImpl implements UsuarioUseCase {
    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario criar(String nome, String email, String senha, Role role) {
        usuarioRepository.buscarPorEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        });

        Usuario usuario = new Usuario(null, nome, email, passwordEncoder.encode(senha), role);
        return usuarioRepository.salvar(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new SenhaIncorretaException();
        }

        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.salvar(usuario);
    }
}
