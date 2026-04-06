package com.triplex.ponto.application.application.usecases;

import com.triplex.ponto.domain.Role;
import com.triplex.ponto.domain.Usuario;

import java.util.List;

public interface UsuarioUseCase {
    Usuario criar(String nome, String email, String senha, Role role);
    List<Usuario> listarTodos();
}
