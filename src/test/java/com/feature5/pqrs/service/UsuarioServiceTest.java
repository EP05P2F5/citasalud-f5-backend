package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void registrarUsuario_emailDuplicado_lanzaExcepcion() {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setDescripcion("USER");

        UsuarioDTO usuario1 = crearUsuarioDTO("user1@test.com", "user1", rolDTO);
        usuarioService.registrarUsuario(usuario1);

        UsuarioDTO usuario2 = crearUsuarioDTO("user1@test.com", "user2", rolDTO);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(usuario2));
    }

    @Test
    void registrarUsuario_nicknameDuplicado_lanzaExcepcion() {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setDescripcion("USER");

        UsuarioDTO usuario1 = crearUsuarioDTO("email1@test.com", "duplicado", rolDTO);
        usuarioService.registrarUsuario(usuario1);

        UsuarioDTO usuario2 = crearUsuarioDTO("email2@test.com", "duplicado", rolDTO);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(usuario2));
    }

    @Test
    void registrarUsuario_rolNoExiste_creaRolAutomaticamente() {
        RolDTO nuevoRol = new RolDTO();
        nuevoRol.setDescripcion("NUEVO_ROL");

        UsuarioDTO usuario = crearUsuarioDTO("test@test.com", "testuser", nuevoRol);
        UsuarioDTO resultado = usuarioService.registrarUsuario(usuario);

        assertEquals("NUEVO_ROL", resultado.getRol().getDescripcion());
        assertTrue(rolRepository.findByDescripcion("NUEVO_ROL").isPresent());
    }

    @Test
    void registrarUsuario_sinRol_asignaUserPorDefecto() {
        UsuarioDTO usuario = crearUsuarioDTO("test@test.com", "testuser", null);
        UsuarioDTO resultado = usuarioService.registrarUsuario(usuario);

        assertEquals("USER", resultado.getRol().getDescripcion());
    }

    private UsuarioDTO crearUsuarioDTO(String email, String nickname, RolDTO rol) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setEmail(email);
        dto.setNickname(nickname);
        dto.setPassword("password123");
        dto.setRol(rol);
        return dto;
    }
}
