package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Usuarios", description = "Microservicio de gestión de usuarios del sistema")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registra un nuevo usuario.
     * Se omite cualquier registro o exposición directa de los datos ingresados por el cliente
     * para prevenir vulnerabilidades de inyección en logs (Sonar rule javasecurity:S5145).
     */
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema con credenciales encriptadas y rol asignado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe", content = @Content())
    })
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.registrarUsuario(usuarioDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    /**
     * Lista todos los usuarios registrados.
     */
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    /**
     * Busca un usuario por su nickname.
     * No se registra ni expone el valor del nickname recibido para evitar logging inseguro.
     */
    @Operation(summary = "Buscar usuario por nickname", description = "Obtiene la información de un usuario específico mediante su nickname")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content())
    })
    @GetMapping("/{nickname}")
    public ResponseEntity<UsuarioDTO> buscarPorNickname(@PathVariable String nickname) {
        UsuarioDTO usuario = usuarioService.buscarPorNickname(nickname);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }
}
