package com.triplex.ponto.infrastructure.persistence.mapper;

import com.triplex.ponto.domain.RefreshToken;
import com.triplex.ponto.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    RefreshToken toDomain(RefreshTokenEntity entity);
    RefreshTokenEntity toEntity(RefreshToken domain);
}
