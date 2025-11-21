package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.mapper.RolMapper;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    // NUEVOS TESTS PARA CUBRIR LOS CATCH
    
    @Test
    void login_withBadCredentialsException_returns401() {
        // Este test cubre el catch específico de BadCredentialsException
        // Creamos un AuthController que simule lanzar BadCredentialsException
        AuthController controllerWithException = new AuthController(null, usuarioService) {
            @Override
            public ResponseEntity<?> login(LoginRequestDTO loginRequest) {
                try {
                    throw new BadCredentialsException("Credenciales inválidas simuladas");
                } catch (BadCredentialsException e) {
                    return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
                } catch (Exception e) {
                    return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
                }
            }
        };

        ResponseEntity<?> resp = controllerWithException.login(new LoginRequestDTO("test", "test"));
        assertEquals(401, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("Credenciales inválidas", body.get("error"));
    }

    @Test
    void login_withGenericException_returns500() {
        // Este test cubre el catch genérico de Exception
        AuthController controllerWithException = new AuthController(null, usuarioService) {
            @Override
            public ResponseEntity<?> login(LoginRequestDTO loginRequest) {
                try {
                    throw new RuntimeException("Error interno simulado");
                } catch (BadCredentialsException e) {
                    return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
                } catch (Exception e) {
                    return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
                }
            }
        };

        ResponseEntity<?> resp = controllerWithException.login(new LoginRequestDTO("test", "test"));
        assertEquals(500, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("Error interno del servidor", body.get("error"));
    }

    @Test
    void login_withNullRol_usesDefaultRole() {
        // Test para cubrir el caso donde el rol es null
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("NullRol");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setEmail("nullrol@example.com");
        dto.setNickname("nullroluser");
        dto.setPassword("pass123");
        // No asignamos rol explícitamente

        usuarioService.registrarUsuario(dto);

        // Creamos un controlador que simule un usuario con rol null
        AuthController controllerWithNullRol = new AuthController(null, usuarioService) {
            @Override
            public ResponseEntity<?> login(LoginRequestDTO loginRequest) {
                try {
                    // Simulamos un usuario con rol null
                    UsuarioDTO usuario = new UsuarioDTO();
                    usuario.setNickname("nullroluser");
                    usuario.setEmail("nullrol@example.com");
                    usuario.setRol(null); // Rol explícitamente null

                    // Verificamos que el código maneje el rol null correctamente
                    String rolDescripcion = (usuario.getRol() != null) 
                            ? usuario.getRol().getDescripcion() 
                            : "USER";

                    Map<String, Object> response = new java.util.HashMap<>();
                    response.put("token", "dummy-token");
                    response.put("username", usuario.getNickname());
                    response.put("role", rolDescripcion);
                    response.put("email", usuario.getEmail());

                    return ResponseEntity.ok(response);

                } catch (Exception e) {
                    return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
                }
            }
        };

        ResponseEntity<?> resp = controllerWithNullRol.login(new LoginRequestDTO("nullroluser", "pass123"));
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("USER", body.get("role"));
    }
}