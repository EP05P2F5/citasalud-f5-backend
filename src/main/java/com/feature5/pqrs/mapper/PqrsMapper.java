package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Pqrs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TipoMapper.class, EstadoMapper.class})
public interface PqrsMapper {

    // Entity → DTO
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "tipo.idTipo", target = "idTipo")
    @Mapping(source = "estado.idEstado", target = "idEstado")
    @Mapping(source = "tipo", target = "tipo")
    @Mapping(source = "estado", target = "estado")
    PqrsDTO toDTO(Pqrs pqrs);

    // DTO → Entity
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Pqrs toEntity(PqrsDTO dto);
}
