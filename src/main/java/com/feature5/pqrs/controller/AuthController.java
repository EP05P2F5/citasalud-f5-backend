package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtils jwtUtils,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint de login: valida las credenciales y genera un token JWT.
     */
    @PostMapping("/login")
    @Transactional(readOnly = true)
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Buscar usuario por nickname
            Usuario usuario = usuarioRepository.findByNickname(loginRequest.getNickname())
                    .orElse(null);

            if (usuario == null) {
                log.warn("Intento de login con usuario inexistente: {}", loginRequest.getNickname());
                return ResponseEntity.status(401)
                        .body(Map.of(ERROR, "Usuario no encontrado"));
            }

            // Validar contraseña
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                log.warn("Credenciales inválidas para usuario: {}", usuario.getNickname());
                return ResponseEntity.status(401)
                        .body(Map.of(ERROR, "Credenciales inválidas"));
            }

            // Inicializar rol para evitar LazyInitializationException
            Rol rol = usuario.getRol();
            String rolDescripcion = (rol != null) ? rol.getDescripcion() : "USER";

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
