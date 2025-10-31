package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.service.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // Lista de todos los roles
    @GetMapping
    public List<RolDTO> listarRoles() {
        return rolService.listarRoles();
    }

    // Obtener rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerRolPorId(@PathVariable Long id) {
        return rolService.obtenerRolPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nuevo rol
    @PostMapping
    public RolDTO crearRol(@RequestBody RolDTO rol) {
        return rolService.crearRol(rol);
    }

    // Actualizar rol existente
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizarRol(@PathVariable Long id, @RequestBody RolDTO rolActualizado) {
        return rolService.actualizarRol(id, rolActualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        if (rolService.eliminarRol(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
