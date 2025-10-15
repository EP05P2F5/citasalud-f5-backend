package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;

    public AuthController(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        UsuarioDTO usuario = usuarioService.login(loginRequest.getNickname(), loginRequest.getPassword());
        if (usuario != null) {
            // 🔑 Generar token JWT
            String token = jwtUtils.generateToken(usuario.getNickname());

            // Devolver token y datos del usuario
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", usuario);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}


