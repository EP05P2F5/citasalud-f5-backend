package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.constants.ResponseKeys;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.feature5.pqrs.constants.ResponseKeys.*;

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

    @PostMapping("/login")
    @Transactional(readOnly = true)
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Buscar usuario
            Usuario usuario = usuarioRepository.findByNickname(loginRequest.getNickname())
                    .orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(401)
                        .body(Map.of(ERROR, "Usuario no encontrado"));
            }

            // Validar contraseña encriptada
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                return ResponseEntity.status(401)
                        .body(Map.of(ERROR, "Credenciales inválidas"));
            }

            // Forzar inicialización del rol para evitar LazyInitializationException
            Rol rol = usuario.getRol();
            String rolDescripcion = (rol != null) ? rol.getDescripcion() : "USER";

            // Generar token JWT
            String token = jwtUtils.generateToken(usuario.getNickname());

            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put(TOKEN, token);
            response.put(USERNAME, usuario.getNickname());
            response.put(ROLE, rolDescripcion);
            response.put(EMAIL, usuario.getEmail());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(Map.of(ERROR, "Credenciales inválidas"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of(ERROR, "Error interno: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
        }
    }
}
