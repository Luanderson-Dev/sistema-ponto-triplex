package com.triplex.ponto.application.application.ports;

import com.triplex.ponto.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Long id);
    Usuario salvar(Usuario usuario);
    List<Usuario> listarTodos();
}
