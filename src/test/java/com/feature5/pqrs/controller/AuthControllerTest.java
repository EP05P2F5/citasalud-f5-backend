package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.RolDTO;
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

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de /auth/login verificando estructura de respuesta:
 * token no vacío, username, rol y email.
 */
@SpringBootTest
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
        rolRepository.deleteAllInBatch();
    }

    @Test
    void login_success_returnsTokenAndUser() {
        // Crear rol base
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_USER");
        rol = rolRepository.save(rol);

        // Registrar usuario con password encriptada (vía servicio)
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle 123");
        dto.setEmail("test@example.com");
        dto.setTelefono("555-0000");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        dto.setRol(rolMapper.toDto(rol));

        usuarioService.registrarUsuario(dto);

        // Login OK
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("testnick", "pass123"));
        assertEquals(200, resp.getStatusCodeValue(), "El login debería responder 200 OK");
        assertTrue(resp.getBody() instanceof Map, "El body del login debe ser un Map");

        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("token"), "Debe incluir 'token'");
        String token = String.valueOf(body.get("token"));
        assertNotNull(token);
        assertFalse(token.isBlank(), "El token no debe estar vacío");

        assertEquals("testnick", body.get("username"), "Debe devolver el mismo nickname");
        assertTrue(body.containsKey("rol"), "Debe incluir 'rol'");
        assertNotNull(body.get("rol"));
        assertTrue(body.containsKey("email"), "Debe incluir 'email'");
        assertEquals("test@example.com", body.get("email"));
    }

    @Test
    void login_fail_wrongPassword_returns401() {
        // Crear rol y usuario
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_USER");
        rolRepository.save(rol);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Fail");
        dto.setApellido("Case");
        dto.setFechaDeNacimiento(LocalDate.of(1992, 2, 2));
        dto.setDireccion("Calle 456");
        dto.setEmail("fail@example.com");
        dto.setTelefono("555-1111");
        dto.setNickname("failnick");
        dto.setPassword("rightpass");
        dto.setRol(rolMapper.toDto(rol));

        usuarioService.registrarUsuario(dto);

        // Contraseña errónea
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("failnick", "wrongpass"));
        assertEquals(401, resp.getStatusCodeValue(), "Con password errónea debe responder 401");
    }

    @Test
    void login_fail_userNotFound_returns401() {
        // Intentar login con usuario inexistente
        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("noexiste", "pass123"));
        assertEquals(401, resp.getStatusCodeValue(), "Usuario no encontrado debe responder 401");
        
        assertTrue(resp.getBody() instanceof Map, "El body debe ser un Map");
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("error"), "Debe incluir mensaje de error");
    }
}
