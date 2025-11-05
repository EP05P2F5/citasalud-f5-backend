package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.EstadoDTO;
import com.feature5.pqrs.entities.Estado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstadoMapper {
    
    EstadoDTO toDto(Estado estado);
    Estado toEntity(EstadoDTO estadoDTO);
}