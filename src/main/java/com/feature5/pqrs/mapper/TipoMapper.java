package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.TipoDTO;
import com.feature5.pqrs.entities.Tipo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoMapper {
    
    TipoDTO toDto(Tipo tipo);
    Tipo toEntity(TipoDTO tipoDTO);
}