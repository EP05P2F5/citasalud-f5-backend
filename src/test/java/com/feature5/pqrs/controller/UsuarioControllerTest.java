package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioControllerTest {

    @Autowired
    UsuarioController usuarioController;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void registerLoginListAndFind() {
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_USER");
        rol = rolRepository.save(rol);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990,1,1));
        dto.setDireccion("Calle 1");
        dto.setEmail("test@example.com");
        dto.setTelefono("123456");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        dto.setRol(rol);

        ResponseEntity<UsuarioDTO> created = usuarioController.registrar(dto);
        assertEquals(200, created.getStatusCodeValue());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getIdUsuario());

        // successful login
        ResponseEntity<UsuarioDTO> loginOk = usuarioController.login("test@example.com", "pass123");
        assertEquals(200, loginOk.getStatusCodeValue());

        // failed login
        ResponseEntity<UsuarioDTO> loginFail = usuarioController.login("test@example.com", "wrong");
        assertEquals(401, loginFail.getStatusCodeValue());

        // list
        ResponseEntity<List<UsuarioDTO>> list = usuarioController.listar();
        assertEquals(1, list.getBody().size());

        // find by nickname
        ResponseEntity<UsuarioDTO> found = usuarioController.buscarPorNickname("testnick");
        assertEquals(200, found.getStatusCodeValue());
        assertEquals("testnick", found.getBody().getNickname());
    }
}
