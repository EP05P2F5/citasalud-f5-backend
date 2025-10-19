package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.mapper.UsuarioMapper;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    UsuarioMapper usuarioMapper;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void registerLoginListFind() {
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_TEST");
        rol = rolRepository.save(rol);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Sergio");
        dto.setApellido("Perez");
        dto.setFechaDeNacimiento(LocalDate.of(1985,5,5));
        dto.setDireccion("Calle X");
        dto.setEmail("sergio@example.com");
        dto.setTelefono("321");
        dto.setNickname("sergio85");
        dto.setPassword("abc123");
        dto.setRol(rol);

        UsuarioDTO saved = usuarioService.registrarUsuario(dto);
        assertNotNull(saved);
        assertNotNull(saved.getIdUsuario());

        UsuarioDTO login = usuarioService.login("sergio85", "abc123");
        assertNotNull(login);
        assertEquals("sergio85", login.getNickname());

        List<UsuarioDTO> all = usuarioService.listarUsuarios();
        assertEquals(1, all.size());

        UsuarioDTO byNick = usuarioService.buscarPorNickname("sergio85");
        assertNotNull(byNick);
        assertEquals("Sergio", byNick.getNombre());
    }
}
