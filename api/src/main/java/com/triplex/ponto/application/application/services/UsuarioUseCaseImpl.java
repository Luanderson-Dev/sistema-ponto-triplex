package com.triplex.ponto.application.application.services;

import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.application.application.usecases.UsuarioUseCase;
import com.triplex.ponto.domain.Role;
import com.triplex.ponto.domain.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioUseCaseImpl implements UsuarioUseCase {
    private final UsuarioRepositoryPort usuarioRepository;

    public UsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public Usuario buscarOuCriarUsuario(String discordId, String nomeUsuario) {
        return usuarioRepository.buscarPorDiscordId(discordId)
                .orElseGet(() -> {
                    String nome = (nomeUsuario != null && !nomeUsuario.isBlank())
                            ? nomeUsuario : "Discord#" + discordId;
                    Usuario novo = new Usuario(
                            null, nome, discordId + "@discord.user", null, Role.USER, discordId
                    );
                    return usuarioRepository.salvar(novo);
                });
    }

    @Override
    public void updateAvatarUrl(String discordId, String avatarUrl) {
        Usuario usuario = usuarioRepository.buscarPorDiscordId(discordId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.setAvatarUrl(avatarUrl);
        usuarioRepository.salvar(usuario);
    }
}
