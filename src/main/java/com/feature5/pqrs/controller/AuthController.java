package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.feature5.pqrs.constants.ResponseKeys.*;

/**
 * Controlador de autenticación (login con JWT).
 * Maneja credenciales seguras y retorna token JWT firmado.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UsuarioService usuarioService;

    public AuthController(JwtUtils jwtUtils,
                          UsuarioService usuarioService) {
        this.jwtUtils = jwtUtils;
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint de login: valida las credenciales y genera un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Autenticar usuario usando el servicio
            UsuarioDTO usuario = usuarioService.login(
                    loginRequest.getNickname(), 
                    loginRequest.getPassword()
            );

            if (usuario == null) {
                log.warn("Intento de login con usuario inexistente: {}", loginRequest.getNickname());
                return ResponseEntity.status(401)
                        .body(Map.of(ERROR, "Credenciales inválidas"));
            }

            // Obtener descripción del rol de forma segura
            String rolDescripcion = (usuario.getRol() != null) 
                    ? usuario.getRol().getDescripcion() 
                    : "USER";

            // Generar token JWT
            String token = jwtUtils.generateToken(usuario.getNickname());

            // Construir respuesta segura
            Map<String, Object> response = new HashMap<>();
            response.put(TOKEN, token);
            response.put(USERNAME, usuario.getNickname());
            response.put(ROLE, rolDescripcion);
            response.put(EMAIL, usuario.getEmail());

            log.info("Usuario autenticado correctamente: {}", usuario.getNickname());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(Map.of(ERROR, "Credenciales inválidas"));

        } catch (Exception e) {
            // Evitar printStackTrace() → usar logger seguro
            log.error("Error interno en login: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of(ERROR, "Error interno del servidor"));
        }
    }
}
