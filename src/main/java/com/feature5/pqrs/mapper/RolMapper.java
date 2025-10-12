package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.entities.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RolMapper {
    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);
    
    RolDTO toDTO(Rol rol);
    Rol toEntity(RolDTO rolDTO);
}
