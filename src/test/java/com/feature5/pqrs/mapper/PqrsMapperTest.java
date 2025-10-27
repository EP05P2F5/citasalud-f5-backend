package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Pqrs;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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

        assertNotNull(dto);
        assertEquals(42L, dto.getIdUsuario());
        assertEquals(1, dto.getIdTipo());
        assertEquals("desc prueba", dto.getDescripcion());
        assertEquals("RAD-123", dto.getRadicado());
        assertEquals("PENDIENTE", dto.getEstado());
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

        assertNotNull(entity);
        assertEquals(99L, entity.getIdUsuario());
        assertEquals(2, entity.getIdTipo());
        assertEquals("otra descripcion", entity.getDescripcion());
        assertEquals("RAD-999", entity.getRadicado());
        assertEquals("CERRADO", entity.getEstado());
    }
}
