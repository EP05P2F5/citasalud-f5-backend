package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsResponseDTO;
import com.feature5.pqrs.entities.Pqrs;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PqrsMapper {
    PqrsMapper INSTANCE = Mappers.getMapper(PqrsMapper.class);
    
    PqrsDTO toDTO(Pqrs pqrs);
    Pqrs toEntity(PqrsDTO pqrsDTO);
    PqrsResponseDTO toResponseDTO(Pqrs pqrs);
}
