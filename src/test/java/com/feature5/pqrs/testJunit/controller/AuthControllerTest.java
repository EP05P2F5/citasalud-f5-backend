package com.feature5.pqrs.testJunit.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.controller.AuthController;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.mapper.RolMapper;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

/**
 * Pruebas de /auth/login verificando estructura de respuesta:
 * token no vacío, username, rol y email.
 */
@SpringBootTest
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private RolMapper rolMapper;

    @SpyBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        // Importante: eliminar primero los que referencian (usuarios) y luego roles.
        usuarioRepository.deleteAllInBatch();
        // No borramos roles porque vienen del script SQL con IDs específicos
    }

    @Test
    void login_success_returnsTokenAndUser() {
        // Registrar usuario con password encriptada (vía servicio)
        // El rol se asigna automáticamente (ID 3)
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle 123");
        dto.setEmail("test@example.com");
        dto.setTelefono("555-0000");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        // No se asigna rol, el sistema asigna automáticamente ID 3

        usuarioService.registrarUsuario(dto);

        // Login OK
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("testnick", "pass123"));
        assertEquals(200, resp.getStatusCode().value(), "El login debería responder 200 OK");
        assertTrue(resp.getBody() instanceof Map, "El body del login debe ser un Map");

        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertNotNull(body.get("token"));
        assertEquals("testnick", body.get("username"));
        assertEquals("test@example.com", body.get("email"));
    }

    @Test
    void login_fail_wrongPassword_returns401() {
        // Registrar usuario (rol ID 3 asignado automáticamente)
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Fail");
        dto.setApellido("Case");
        dto.setFechaDeNacimiento(LocalDate.of(1992, 2, 2));
        dto.setDireccion("Calle 456");
        dto.setEmail("fail@example.com");
        dto.setTelefono("555-1111");
        dto.setNickname("failnick");
        dto.setPassword("rightpass");
        // No se asigna rol, el sistema asigna automáticamente ID 3

        usuarioService.registrarUsuario(dto);

        // Contraseña errónea
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("failnick", "wrongpass"));
        assertEquals(401, resp.getStatusCode().value(), "Con password errónea debe responder 401");
    }

    @Test
    void login_fail_userNotFound_returns401() {
        // Intentar login con usuario inexistente
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("noexiste", "pass123"));
        assertEquals(401, resp.getStatusCode().value(), "Usuario no encontrado debe responder 401");

        assertTrue(resp.getBody() instanceof Map, "El body debe ser un Map");
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("error"), "Debe incluir mensaje de error");
    }

    @Test
    void login_fail_unexpectedException_returns500() {
        // Registrar usuario primero
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Error");
        dto.setApellido("Test");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle 789");
        dto.setEmail("error@example.com");
        dto.setTelefono("555-2222");
        dto.setNickname("erroruser");
        dto.setPassword("pass123");

        usuarioService.registrarUsuario(dto);

        // Forzar excepción en generateToken usando Mockito
        doThrow(new RuntimeException("JWT generation failed"))
                .when(jwtUtils).generateToken(anyString());

        // Intentar login, debería capturar la excepción
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("erroruser", "pass123"));
        
        assertEquals(500, resp.getStatusCode().value(), "Excepción inesperada debe responder 500");
        assertTrue(resp.getBody() instanceof Map, "El body debe ser un Map");
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("error"), "Debe incluir mensaje de error");
        assertEquals("Error interno del servidor", body.get("error"));
    }
}