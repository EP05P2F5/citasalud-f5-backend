package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.DTO.UsuarioResponseDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioMapperTest {

    @Test
    public void testToDTO_andToResponseDTO() {
        UsuarioMapper mapper = UsuarioMapper.INSTANCE;

        Usuario user = new Usuario();
        user.setIdUsuario(42L);
        user.setNombre("Juan");
        user.setApellido("Perez");
        user.setEmail("juan@example.com");
        user.setNickname("juanp");
        user.setPassword("secret");
        user.setTelefono("12345");
        user.setFechaDeNacimiento(LocalDate.of(1990,1,1));
        Rol rol = new Rol();
        rol.setIdRol(2L);
        rol.setDescripcion("ROLE_USER");
        user.setRol(rol);

        UsuarioDTO dto = mapper.toDTO(user);
        assertNotNull(dto);
        assertEquals(user.getIdUsuario(), dto.getIdUsuario());
        assertEquals(user.getNombre(), dto.getNombre());
        assertEquals(user.getNickname(), dto.getNickname());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getRol(), dto.getRol());

        UsuarioResponseDTO resp = mapper.toResponseDTO(user);
        assertNotNull(resp);
        assertEquals(user.getIdUsuario(), resp.getIdUsuario());
        assertEquals(user.getNickname(), resp.getNickname());
        assertEquals(user.getEmail(), resp.getEmail());
        assertEquals(user.getRol(), resp.getRol());
    }
}
