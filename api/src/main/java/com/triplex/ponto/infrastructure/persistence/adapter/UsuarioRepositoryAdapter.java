package com.triplex.ponto.infrastructure.persistence.adapter;

import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.infrastructure.persistence.entity.UsuarioEntity;
import com.triplex.ponto.infrastructure.persistence.mapper.UsuarioMapper;
import com.triplex.ponto.infrastructure.persistence.repository.SpringUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {
    private final SpringUsuarioRepository repository;
    private final UsuarioMapper mapper;

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorDiscordId(String discordId) {
        return repository.findByDiscordId(discordId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Usuario> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
