package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PqrsMapper {

    // Entity → DTO
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "tipo.idTipo", target = "idTipo")
    @Mapping(source = "estado.descripcion", target = "estado")
    PqrsDTO toDTO(Pqrs pqrs);

    // DTO → Entity
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(source = "estado", target = "estado", qualifiedByName = "stringToEstado")
    Pqrs toEntity(PqrsDTO dto);

    @Named("estadoToString")
    default String estadoToString(Estado estado) {
        return estado != null ? estado.getDescripcion() : null;
    }

    @Named("stringToEstado")
    default Estado stringToEstado(String descripcion) {
        if (descripcion == null) return null;
        Estado e = new Estado();
        e.setDescripcion(descripcion);
        return e;
    }
}
