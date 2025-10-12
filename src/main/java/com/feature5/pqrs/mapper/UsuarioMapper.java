package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.DTO.UsuarioResponseDTO;
import com.feature5.pqrs.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);
    
    UsuarioDTO toDTO(Usuario usuario);
    Usuario toEntity(UsuarioDTO dto);
    UsuarioResponseDTO toResponseDTO(Usuario usuario);
}


