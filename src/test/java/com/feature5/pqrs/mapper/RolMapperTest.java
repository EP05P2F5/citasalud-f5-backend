package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.entities.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RolMapperTest {

    @Autowired
    private RolMapper rolMapper;

    @Test
    void testToDTO_DebeConvertirRolADTO() {
        // Arrange
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setDescripcion("ADMIN");

        // Act
        RolDTO resultado = rolMapper.toDTO(rol);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
        assertEquals("ADMIN", resultado.getDescripcion());
    }

    @Test
    void testToEntity_DebeConvertirDTOARol() {
        // Arrange
        RolDTO dto = new RolDTO();
        dto.setIdRol(2L);
        dto.setDescripcion("USER");

        // Act
        Rol resultado = rolMapper.toEntity(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(2L, resultado.getIdRol());
        assertEquals("USER", resultado.getDescripcion());
    }

    @Test
    void testToDTO_ConNulos_DebeRetornarNull() {
        // Act
        RolDTO resultado = rolMapper.toDTO(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void testToEntity_ConNulos_DebeRetornarNull() {
        // Act
        Rol resultado = rolMapper.toEntity(null);

        // Assert
        assertNull(resultado);
    }
}
