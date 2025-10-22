package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.entities.Rol;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-22T00:01:43-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Eclipse Adoptium)"
)
@Component
public class RolMapperImpl implements RolMapper {

    @Override
    public RolDTO toDTO(Rol rol) {
        if ( rol == null ) {
            return null;
        }

        RolDTO rolDTO = new RolDTO();

        rolDTO.setIdRol( rol.getIdRol() );
        rolDTO.setDescripcion( rol.getDescripcion() );

        return rolDTO;
    }

    @Override
    public Rol toEntity(RolDTO rolDTO) {
        if ( rolDTO == null ) {
            return null;
        }

        Rol rol = new Rol();

        rol.setIdRol( rolDTO.getIdRol() );
        rol.setDescripcion( rolDTO.getDescripcion() );

        return rol;
    }
}
