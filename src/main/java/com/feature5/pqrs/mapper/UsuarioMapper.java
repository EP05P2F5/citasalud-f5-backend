package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RolMapper.class})
public interface UsuarioMapper {

    // Convierte una entidad Usuario a DTO (oculta el password por @JsonIgnore)
    UsuarioDTO toDto(Usuario usuario);

    // Convierte un DTO a entidad Usuario (para guardar en BD)
    Usuario toEntity(UsuarioDTO dto);
}
