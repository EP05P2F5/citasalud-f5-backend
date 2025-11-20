package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @Operation(summary = "Registrar nuevo usuario (autenticado)", description = "Crea un nuevo usuario en el sistema. Requiere autenticación. Si el cuerpo incluye rol.idRol, se usará el rol indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe", content = @Content()),
    })
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioDTO usuarioDTO,
                                                Authentication authentication) {
        // Si el request viene autenticado, permitir que se asigne el rol
        // enviado en el DTO (si está presente). La ruta ya requiere
        // autenticación según la configuración de seguridad.
        Integer roleIdToUse = null;
        if (authentication != null && authentication.isAuthenticated()) {
            if (usuarioDTO.getRol() != null && usuarioDTO.getRol().getIdRol() != null) {
                roleIdToUse = usuarioDTO.getRol().getIdRol();
            }
        }

        UsuarioDTO nuevoUsuario = usuarioService.registrarUsuarioWithRole(usuarioDTO, roleIdToUse);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    /**
     * Endpoint público para registro estándar: siempre crea usuario con rol por defecto (ID = 3).
     * Ruta pública: POST /usuarios/registrar
     */
    @Operation(summary = "Registrar nuevo usuario (público)", description = "Registro público estándar. Siempre crea usuario con rol por defecto (ID = 3). No requiere autenticación.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe", content = @Content())
    })
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrarPublic(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.registrarUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
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
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UsuarioDTO> buscarPorNickname(@PathVariable String nickname) {
        UsuarioDTO usuario = usuarioService.buscarPorNickname(nickname);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Listar gestores", description = "Obtiene los usuarios que tienen el rol Gestor (IdRol = 2)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de gestores obtenida exitosamente")
    })
    @GetMapping("/gestores")
    public ResponseEntity<List<UsuarioDTO>> listarGestores() {
        return ResponseEntity.ok(usuarioService.listarGestores());
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente. No permite cambiar el nickname.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o conflicto (email/nickname)", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarPorId(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            var actualizadoOpt = usuarioService.actualizarUsuarioPorId(id, usuarioDTO);
            if (actualizadoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(actualizadoOpt.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario existente identificado por su nickname")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) {
        if (usuarioService.eliminarUsuarioPorId(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene la información de un usuario específico mediante su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }
}
