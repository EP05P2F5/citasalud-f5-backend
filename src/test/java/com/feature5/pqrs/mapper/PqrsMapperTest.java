package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsResponseDTO;
import com.feature5.pqrs.entities.Pqrs;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PqrsMapperTest {

    @Test
    void toDTO_shouldMapFields() {
        Pqrs pqrs = new Pqrs();
        pqrs.setIdUsuario(42L);
        pqrs.setIdTipo(1);
        pqrs.setDescripcion("desc prueba");
        pqrs.setFechaDeGeneracion(LocalDate.of(2024,1,2));
        pqrs.setRadicado("RAD-123");
        pqrs.setEstado("PENDIENTE");

        PqrsDTO dto = PqrsMapper.INSTANCE.toDTO(pqrs);

        assertThat(dto).isNotNull();
        assertThat(dto.getIdUsuario()).isEqualTo(42L);
        assertThat(dto.getIdTipo()).isEqualTo(1);
        assertThat(dto.getDescripcion()).isEqualTo("desc prueba");
        assertThat(dto.getRadicado()).isEqualTo("RAD-123");
        assertThat(dto.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void toEntity_shouldMapFields() {
        PqrsDTO dto = new PqrsDTO();
        dto.setIdUsuario(99L);
    dto.setIdTipo(2);
        dto.setDescripcion("otra descripcion");
        dto.setRadicado("RAD-999");
        dto.setEstado("CERRADO");

        Pqrs entity = PqrsMapper.INSTANCE.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getIdUsuario()).isEqualTo(99L);
        assertThat(entity.getIdTipo()).isEqualTo(2);
        assertThat(entity.getDescripcion()).isEqualTo("otra descripcion");
        assertThat(entity.getRadicado()).isEqualTo("RAD-999");
        assertThat(entity.getEstado()).isEqualTo("CERRADO");
    }

    @Test
    void toResponseDTO_shouldMapFields() {
        Pqrs pqrs = new Pqrs();
        pqrs.setIdUsuario(7L);
    pqrs.setIdTipo(3);
        pqrs.setDescripcion("respuesta desc");
        pqrs.setFechaDeGeneracion(LocalDate.of(2025,6,7));
        pqrs.setRadicado("RAD-7");
        pqrs.setEstado("RESPONDIDO");
        pqrs.setFechaDeRespuesta(LocalDate.of(2025,7,1));
        pqrs.setRespuesta("Hecho");

        PqrsResponseDTO resp = PqrsMapper.INSTANCE.toResponseDTO(pqrs);

        assertThat(resp).isNotNull();
        assertThat(resp.getIdTipo()).isEqualTo(3);
        assertThat(resp.getDescripcion()).isEqualTo("respuesta desc");
        assertThat(resp.getRadicado()).isEqualTo("RAD-7");
        assertThat(resp.getEstado()).isEqualTo("RESPONDIDO");
        assertThat(resp.getRespuesta()).isEqualTo("Hecho");
        assertThat(resp.getFechaDeRespuesta()).isEqualTo(LocalDate.of(2025,7,1));
    }
}
