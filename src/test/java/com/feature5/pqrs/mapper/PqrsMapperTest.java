package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PqrsMapperTest {

    @Autowired
    private PqrsMapper mapper;

    @Test
    void toDTO_shouldMapFields() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(42L);

        Tipo tipo = new Tipo();
        tipo.setIdTipo(1);
        tipo.setDescripcion("Queja");

        Estado estado = new Estado();
        estado.setIdEstado(1);
        estado.setDescripcion("PENDIENTE");

        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setDescripcion("desc prueba");
        pqrs.setFechaDeGeneracion(LocalDateTime.of(2024, 1, 2, 0, 0));
        pqrs.setRadicado("RAD-123");
        pqrs.setEstado(estado);

        PqrsDTO dto = mapper.toDTO(pqrs);

        assertNotNull(dto);
        assertEquals(42L, dto.getIdUsuario());
        assertEquals(1, dto.getIdTipo());
        assertEquals("desc prueba", dto.getDescripcion());
        assertEquals("RAD-123", dto.getRadicado());
        assertEquals(1, dto.getIdEstado());
        
        // Verificar que los objetos completos están presentes
        assertNotNull(dto.getTipo());
        assertEquals(1, dto.getTipo().getIdTipo());
        assertEquals("Queja", dto.getTipo().getDescripcion());
        
        assertNotNull(dto.getEstado());
        assertEquals(1, dto.getEstado().getIdEstado());
        assertEquals("PENDIENTE", dto.getEstado().getDescripcion());
    }

    @Test
    void toEntity_shouldMapFields() {
        PqrsDTO dto = new PqrsDTO();
        dto.setIdUsuario(99L);
        dto.setIdTipo(2);
        dto.setDescripcion("otra descripcion");
        dto.setRadicado("RAD-999");
        dto.setIdEstado(2);

        Pqrs entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("otra descripcion", entity.getDescripcion());
        assertEquals("RAD-999", entity.getRadicado());
        // El estado será ignorado en el mapeo, como configuramos
        assertNull(entity.getEstado());
    }
}
