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
        usuarioService.registrarUsuario(crearUsuarioDTO("user@test.com", "user1", null));
        assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.registrarUsuario(crearUsuarioDTO("user@test.com", "user2", null)));
    }

    @Test
    void registrarUsuario_nicknameDuplicado_lanzaExcepcion() {
        usuarioService.registrarUsuario(crearUsuarioDTO("email1@test.com", "duplicado", null));
        assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.registrarUsuario(crearUsuarioDTO("email2@test.com", "duplicado", null)));
    }

    @Test
    void registrarUsuario_asignaRolUsuarioPorDefecto() {
        UsuarioDTO resultado = usuarioService.registrarUsuario(crearUsuarioDTO("test@test.com", "testuser", null));
        assertEquals(3, resultado.getRol().getIdRol());
        assertEquals("Usuario", resultado.getRol().getDescripcion());
    }

    @Test
    void registrarUsuarioWithRole_conRolEspecifico_asignaRolCorrectamente() {
        UsuarioDTO resultado = usuarioService.registrarUsuarioWithRole(crearUsuarioDTO("gestor@test.com", "gestoruser", null), 2);
        assertEquals(2, resultado.getRol().getIdRol());
        assertEquals("Gestor", resultado.getRol().getDescripcion());
    }

    @Test
    void actualizarUsuarioPorId_cambiarEmail_emailDuplicado_lanzaExcepcion() {
        UsuarioDTO guardado1 = usuarioService.registrarUsuario(crearUsuarioDTO("email1@test.com", "user1", null));
        usuarioService.registrarUsuario(crearUsuarioDTO("email2@test.com", "user2", null));

        UsuarioDTO actualizacion = new UsuarioDTO();
        actualizacion.setEmail("email2@test.com");

        assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.actualizarUsuarioPorId(guardado1.getIdUsuario(), actualizacion));
    }

    @Test
    void actualizarUsuarioPorId_cambiarEmailPasswordYRol_actualizaCorrectamente() {
        UsuarioDTO guardado = usuarioService.registrarUsuario(crearUsuarioDTO("old@test.com", "testuser", null));

        // Actualizar email, password y rol
        RolDTO nuevoRol = new RolDTO();
        nuevoRol.setIdRol(2); // Gestor
        UsuarioDTO actualizacion = new UsuarioDTO();
        actualizacion.setEmail("new@test.com");
        actualizacion.setPassword("newPassword123");
        actualizacion.setRol(nuevoRol);

        usuarioService.actualizarUsuarioPorId(guardado.getIdUsuario(), actualizacion);

        // Verificar email y rol
        UsuarioDTO actualizado = usuarioService.buscarPorId(guardado.getIdUsuario());
        assertEquals("new@test.com", actualizado.getEmail());
        assertEquals(2, actualizado.getRol().getIdRol());
        assertEquals("Gestor", actualizado.getRol().getDescripcion());
        
        // Verificar password con login
        assertNotNull(usuarioService.login("testuser", "newPassword123"));
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
