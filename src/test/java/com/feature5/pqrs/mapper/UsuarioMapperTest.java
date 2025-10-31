package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioMapperTest {

    @Autowired
    private UsuarioMapper mapper;

    @Test
    void testToDTO() {
        Usuario user = new Usuario();
        user.setIdUsuario(1L);
        user.setNombre("Juan");
        user.setApellido("Pérez");
        user.setEmail("juan@example.com");
        user.setNickname("juanp");
        user.setPassword("secret");
        user.setTelefono("12345");
        user.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));

        Rol rol = new Rol();
        rol.setIdRol(2L);
        rol.setDescripcion("ROLE_USER");
        user.setRol(rol);

        UsuarioDTO dto = mapper.toDto(user);

        assertNotNull(dto);
        assertEquals(user.getNombre(), dto.getNombre());
        assertEquals(user.getApellido(), dto.getApellido());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getNickname(), dto.getNickname());
        assertEquals(user.getTelefono(), dto.getTelefono());
        assertEquals(user.getRol().getDescripcion(), dto.getRol().getDescripcion());
    }

    @Test
    void testToEntity() {
        RolDTO rol = new RolDTO();
        rol.setIdRol(3L);
        rol.setDescripcion("ADMIN");

        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(1L);
        dto.setNombre("María");
        dto.setApellido("García");
        dto.setEmail("maria@example.com");
        dto.setNickname("mariag");
        dto.setPassword("pass456");
        dto.setTelefono("67890");
        dto.setFechaDeNacimiento(LocalDate.of(1995, 5, 15));
        dto.setRol(rol);

        Usuario entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getNombre(), entity.getNombre());
        assertEquals(dto.getApellido(), entity.getApellido());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getNickname(), entity.getNickname());
        assertEquals(dto.getPassword(), entity.getPassword());
        assertEquals(dto.getTelefono(), entity.getTelefono());
        assertEquals(dto.getFechaDeNacimiento(), entity.getFechaDeNacimiento());
        assertEquals(dto.getRol().getDescripcion(), entity.getRol().getDescripcion());
    }
}
