package com.triplex.ponto.infrastructure.persistence.mapper;

import com.triplex.ponto.domain.Usuario;
import com.triplex.ponto.infrastructure.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toDomain(UsuarioEntity entity);
    UsuarioEntity toEntity(Usuario domain);
}
