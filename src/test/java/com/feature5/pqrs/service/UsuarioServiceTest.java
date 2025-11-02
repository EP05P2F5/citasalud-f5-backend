package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
        // No borramos roles porque vienen del script SQL con IDs específicos
    }

    @Test
    void registrarUsuario_emailDuplicado_lanzaExcepcion() {
        // El rol enviado se ignora, siempre se asigna rol ID 3
        UsuarioDTO usuario1 = crearUsuarioDTO("user1@test.com", "user1", null);
        usuarioService.registrarUsuario(usuario1);

        UsuarioDTO usuario2 = crearUsuarioDTO("user1@test.com", "user2", null);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(usuario2));
    }

    @Test
    void registrarUsuario_nicknameDuplicado_lanzaExcepcion() {
        // El rol enviado se ignora, siempre se asigna rol ID 3
        UsuarioDTO usuario1 = crearUsuarioDTO("email1@test.com", "duplicado", null);
        usuarioService.registrarUsuario(usuario1);

        UsuarioDTO usuario2 = crearUsuarioDTO("email2@test.com", "duplicado", null);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(usuario2));
    }

    @Test
    void registrarUsuario_rolNoExiste_creaRolAutomaticamente() {
        // Este test ya no aplica: los usuarios siempre reciben rol ID 3
        // No importa qué rol se envíe, siempre se asigna rol ID 3 (Usuario)
        RolDTO cualquierRol = new RolDTO();
        cualquierRol.setDescripcion("NUEVO_ROL");

        UsuarioDTO usuario = crearUsuarioDTO("test@test.com", "testuser", cualquierRol);
        UsuarioDTO resultado = usuarioService.registrarUsuario(usuario);

        // Verifica que se asignó rol ID 3 (Usuario), no el rol enviado
        assertEquals("Usuario", resultado.getRol().getDescripcion());
        assertEquals(3L, resultado.getRol().getIdRol());
    }

    @Test
    void registrarUsuario_sinRol_asignaUserPorDefecto() {
        UsuarioDTO usuario = crearUsuarioDTO("test@test.com", "testuser", null);
        UsuarioDTO resultado = usuarioService.registrarUsuario(usuario);

        assertEquals("Usuario", resultado.getRol().getDescripcion());
        assertEquals(3L, resultado.getRol().getIdRol());
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
