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
        rol.setIdRol(1);
        rol.setDescripcion("ADMIN");

        RolDTO resultado = rolMapper.toDto(rol);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdRol().intValue());
        assertEquals("ADMIN", resultado.getDescripcion());
    }

    @Test
    void testToEntity_DebeConvertirDTOARol() {
        RolDTO dto = new RolDTO();
        dto.setIdRol(2);
        dto.setDescripcion("USER");

        Rol resultado = rolMapper.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(2, resultado.getIdRol().intValue());
        assertEquals("USER", resultado.getDescripcion());
    }
}
