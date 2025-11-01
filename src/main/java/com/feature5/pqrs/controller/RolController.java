package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Roles", description = "Microservicio de gestión de roles y permisos del sistema")
@RestController
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @Operation(
        summary = "Listar todos los roles",
        description = "Obtiene la lista completa de roles disponibles en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de roles obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = RolDTO.class))
        )
    })
    @GetMapping
    public List<RolDTO> listarRoles() {
        return rolService.listarRoles();
    }

    @Operation(
        summary = "Obtener rol por ID",
        description = "Obtiene la información de un rol específico mediante su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Rol encontrado",
            content = @Content(schema = @Schema(implementation = RolDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerRolPorId(
            @Parameter(description = "ID del rol a obtener")
            @PathVariable Long id) {
        return rolService.obtenerRolPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Crear nuevo rol",
        description = "Registra un nuevo rol en el sistema con su descripción"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Rol creado exitosamente",
            content = @Content(schema = @Schema(implementation = RolDTO.class))
        )
    })
    @PostMapping
    public RolDTO crearRol(
            @Parameter(description = "Datos del nuevo rol (descripción)")
            @RequestBody RolDTO rol) {
        return rolService.crearRol(rol);
    }

    @Operation(
        summary = "Actualizar rol existente",
        description = "Modifica la información de un rol existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Rol actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = RolDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizarRol(
            @Parameter(description = "ID del rol a actualizar")
            @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del rol")
            @RequestBody RolDTO rolActualizado) {
        return rolService.actualizarRol(id, rolActualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Eliminar rol",
        description = "Elimina un rol del sistema de forma permanente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(
            @Parameter(description = "ID del rol a eliminar")
            @PathVariable Long id) {
        if (rolService.eliminarRol(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
