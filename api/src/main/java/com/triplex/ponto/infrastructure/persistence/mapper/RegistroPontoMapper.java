package com.triplex.ponto.infrastructure.persistence.mapper;

import com.triplex.ponto.domain.RegistroPonto;
import com.triplex.ponto.infrastructure.persistence.entity.RegistroPontoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistroPontoMapper {
    RegistroPonto toDomain(RegistroPontoEntity entity);
    RegistroPontoEntity toEntity(RegistroPonto domain);
    List<RegistroPonto> toDomainList(List<RegistroPontoEntity> entities);
}
