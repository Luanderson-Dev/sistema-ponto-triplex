package com.triplex.ponto.application.application.usecases;

import com.triplex.ponto.domain.Role;
import com.triplex.ponto.domain.Usuario;

import java.util.List;

public interface UsuarioUseCase {
    List<Usuario> listarTodos();
    Usuario buscarOuCriarUsuario(String discordId, String nomeUsuario);
    void updateAvatarUrl(String discordId, String avatarUrl);
    Usuario buscarUsuarioPorDiscordId(String discordId);
    boolean sincronizarDadosDiscord(String discordId, String nomeUsuario, String avatarUrl);
}
