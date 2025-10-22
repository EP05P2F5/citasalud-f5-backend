package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsResponseDTO;
import com.feature5.pqrs.entities.Pqrs;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-22T00:06:52-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class PqrsMapperImpl implements PqrsMapper {

    @Override
    public PqrsDTO toDTO(Pqrs pqrs) {
        if ( pqrs == null ) {
            return null;
        }

        PqrsDTO pqrsDTO = new PqrsDTO();

        pqrsDTO.setDescripcion( pqrs.getDescripcion() );
        pqrsDTO.setEstado( pqrs.getEstado() );
        pqrsDTO.setFechaDeGeneracion( pqrs.getFechaDeGeneracion() );
        pqrsDTO.setFechaDeRespuesta( pqrs.getFechaDeRespuesta() );
        pqrsDTO.setIdPqrs( pqrs.getIdPqrs() );
        pqrsDTO.setIdTipo( pqrs.getIdTipo() );
        pqrsDTO.setIdUsuario( pqrs.getIdUsuario() );
        pqrsDTO.setRadicado( pqrs.getRadicado() );
        pqrsDTO.setRespuesta( pqrs.getRespuesta() );

        return pqrsDTO;
    }

    @Override
    public Pqrs toEntity(PqrsDTO pqrsDTO) {
        if ( pqrsDTO == null ) {
            return null;
        }

        Pqrs pqrs = new Pqrs();

        pqrs.setDescripcion( pqrsDTO.getDescripcion() );
        pqrs.setEstado( pqrsDTO.getEstado() );
        pqrs.setFechaDeGeneracion( pqrsDTO.getFechaDeGeneracion() );
        pqrs.setFechaDeRespuesta( pqrsDTO.getFechaDeRespuesta() );
        pqrs.setIdTipo( pqrsDTO.getIdTipo() );
        pqrs.setIdUsuario( pqrsDTO.getIdUsuario() );
        pqrs.setRadicado( pqrsDTO.getRadicado() );
        pqrs.setRespuesta( pqrsDTO.getRespuesta() );

        return pqrs;
    }

    @Override
    public PqrsResponseDTO toResponseDTO(Pqrs pqrs) {
        if ( pqrs == null ) {
            return null;
        }

        PqrsResponseDTO pqrsResponseDTO = new PqrsResponseDTO();

        pqrsResponseDTO.setDescripcion( pqrs.getDescripcion() );
        pqrsResponseDTO.setEstado( pqrs.getEstado() );
        pqrsResponseDTO.setFechaDeGeneracion( pqrs.getFechaDeGeneracion() );
        pqrsResponseDTO.setFechaDeRespuesta( pqrs.getFechaDeRespuesta() );
        pqrsResponseDTO.setIdPqrs( pqrs.getIdPqrs() );
        pqrsResponseDTO.setIdTipo( pqrs.getIdTipo() );
        pqrsResponseDTO.setRadicado( pqrs.getRadicado() );
        pqrsResponseDTO.setRespuesta( pqrs.getRespuesta() );

        return pqrsResponseDTO;
    }
}
