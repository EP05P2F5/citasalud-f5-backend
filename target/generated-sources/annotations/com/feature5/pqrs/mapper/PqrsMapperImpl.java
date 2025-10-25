package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsResponseDTO;
import com.feature5.pqrs.entities.Pqrs;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-24T21:00:27-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Eclipse Adoptium)"
)
@Component
public class PqrsMapperImpl implements PqrsMapper {

    @Override
    public PqrsDTO toDTO(Pqrs pqrs) {
        if ( pqrs == null ) {
            return null;
        }

        PqrsDTO pqrsDTO = new PqrsDTO();

        pqrsDTO.setIdPqrs( pqrs.getIdPqrs() );
        pqrsDTO.setIdUsuario( pqrs.getIdUsuario() );
        pqrsDTO.setIdTipo( pqrs.getIdTipo() );
        pqrsDTO.setDescripcion( pqrs.getDescripcion() );
        pqrsDTO.setFechaDeGeneracion( pqrs.getFechaDeGeneracion() );
        pqrsDTO.setRadicado( pqrs.getRadicado() );
        pqrsDTO.setEstado( pqrs.getEstado() );
        pqrsDTO.setFechaDeRespuesta( pqrs.getFechaDeRespuesta() );
        pqrsDTO.setRespuesta( pqrs.getRespuesta() );

        return pqrsDTO;
    }

    @Override
    public Pqrs toEntity(PqrsDTO pqrsDTO) {
        if ( pqrsDTO == null ) {
            return null;
        }

        Pqrs pqrs = new Pqrs();

        pqrs.setIdUsuario( pqrsDTO.getIdUsuario() );
        pqrs.setIdTipo( pqrsDTO.getIdTipo() );
        pqrs.setDescripcion( pqrsDTO.getDescripcion() );
        pqrs.setFechaDeGeneracion( pqrsDTO.getFechaDeGeneracion() );
        pqrs.setRadicado( pqrsDTO.getRadicado() );
        pqrs.setEstado( pqrsDTO.getEstado() );
        pqrs.setFechaDeRespuesta( pqrsDTO.getFechaDeRespuesta() );
        pqrs.setRespuesta( pqrsDTO.getRespuesta() );

        return pqrs;
    }

    @Override
    public PqrsResponseDTO toResponseDTO(Pqrs pqrs) {
        if ( pqrs == null ) {
            return null;
        }

        PqrsResponseDTO pqrsResponseDTO = new PqrsResponseDTO();

        pqrsResponseDTO.setIdPqrs( pqrs.getIdPqrs() );
        pqrsResponseDTO.setIdTipo( pqrs.getIdTipo() );
        pqrsResponseDTO.setDescripcion( pqrs.getDescripcion() );
        pqrsResponseDTO.setFechaDeGeneracion( pqrs.getFechaDeGeneracion() );
        pqrsResponseDTO.setRadicado( pqrs.getRadicado() );
        pqrsResponseDTO.setEstado( pqrs.getEstado() );
        pqrsResponseDTO.setFechaDeRespuesta( pqrs.getFechaDeRespuesta() );
        pqrsResponseDTO.setRespuesta( pqrs.getRespuesta() );

        return pqrsResponseDTO;
    }
}
