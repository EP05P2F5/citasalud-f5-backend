package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtUtils jwtUtils;

    private AuthController authController;

    @BeforeEach
    void setup() {
        authController = new AuthController(jwtUtils, usuarioService);
    }

    @Test
    void login_success_returnsTokenAndUser() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setDireccion("Calle 123");
        usuario.setEmail("test@example.com");
        usuario.setTelefono("555-0000");
        usuario.setNickname("testnick");

        // Asignar rol usando constructor para garantizar valores correctos
        RolDTO rolDto = new RolDTO(3, "ROLE_USER");
        usuario.setRol(rolDto);

        when(usuarioService.login("testnick", "pass123")).thenReturn(usuario);
        when(jwtUtils.generateToken("testnick")).thenReturn("tok-123");

        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("testnick", "pass123"));

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("tok-123", body.get("token"));
        assertEquals("testnick", body.get("username"));
        assertEquals("test@example.com", body.get("email"));
        assertEquals("ROLE_USER", body.get("rol"));
    }

    @Test
    void login_fail_wrongPassword_returns401() {
        // Servicio retorna null cuando credenciales inválidas
        when(usuarioService.login("failnick", "wrongpass")).thenReturn(null);

        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("failnick", "wrongpass"));

        assertEquals(401, resp.getStatusCodeValue());
        assertTrue(resp.getBody() instanceof Map);
    }

    @Test
    void login_fail_userNotFound_returns401() {
        when(usuarioService.login("noexiste", "pass123")).thenReturn(null);

        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("noexiste", "pass123"));

        assertEquals(401, resp.getStatusCodeValue());
        assertTrue(resp.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("error"));
    }

    @Test
    void login_fail_unexpectedException_returns500() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNickname("erroruser");
        usuario.setEmail("error@example.com");
        when(usuarioService.login("erroruser", "pass123")).thenReturn(usuario);

        doThrow(new RuntimeException("JWT generation failed"))
                .when(jwtUtils).generateToken(anyString());

        ResponseEntity<?> resp = authController.login(new LoginRequestDTO("erroruser", "pass123"));

        assertEquals(500, resp.getStatusCodeValue());
        assertTrue(resp.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("error"));
        assertEquals("Error interno del servidor", body.get("error"));
    }
}