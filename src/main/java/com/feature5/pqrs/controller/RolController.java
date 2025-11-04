package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(summary = "Listar todos los roles", description = "Obtiene la lista completa de roles disponibles en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente")
    @GetMapping
    public List<RolDTO> listarRoles() {
        return rolService.listarRoles();
    }

    @Operation(summary = "Obtener rol por ID", description = "Obtiene la información de un rol específico mediante su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol encontrado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerRolPorId(@PathVariable Integer id) {
        return rolService.obtenerRolPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear nuevo rol", description = "Registra un nuevo rol en el sistema con su descripción")
    @ApiResponse(responseCode = "200", description = "Rol creado exitosamente")
    @PostMapping
    public RolDTO crearRol(@RequestBody RolDTO rol) {
        return rolService.crearRol(rol);
    }

    @Operation(summary = "Actualizar rol existente", description = "Modifica la información de un rol existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizarRol(@PathVariable Integer id, @RequestBody RolDTO rolActualizado) {
        return rolService.actualizarRol(id, rolActualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar rol", description = "Elimina un rol del sistema de forma permanente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        if (rolService.eliminarRol(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
