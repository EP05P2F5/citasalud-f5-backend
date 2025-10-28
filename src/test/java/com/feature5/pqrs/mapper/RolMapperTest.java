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
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setDescripcion("ADMIN");

        RolDTO resultado = rolMapper.toDTO(rol);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
        assertEquals("ADMIN", resultado.getDescripcion());
    }

    @Test
    void testToEntity_DebeConvertirDTOARol() {
        RolDTO dto = new RolDTO();
        dto.setIdRol(2L);
        dto.setDescripcion("USER");

        Rol resultado = rolMapper.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getIdRol());
        assertEquals("USER", resultado.getDescripcion());
    }
}
