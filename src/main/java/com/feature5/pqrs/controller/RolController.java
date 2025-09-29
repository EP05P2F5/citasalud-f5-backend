package com.feature5.pqrs.controller;

import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    //Lista de todos los roles
    @GetMapping
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    //Obtener rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerRolPorId(@PathVariable Long id) {
        Optional<Rol> rol = rolRepository.findById(id);
        return rol.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Crear nuevo rol
    @PostMapping
    public Rol crearRol(@RequestBody Rol rol) {
        return rolRepository.save(rol);
    }

    //Actualizar rol existente
    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rolActualizado) {
        Optional<Rol> rolExistente = rolRepository.findById(id);

        if (rolExistente.isPresent()) {
            Rol rol = rolExistente.get();
            rol.setDescripcion(rolActualizado.getDescripcion());
            return ResponseEntity.ok(rolRepository.save(rol));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Eliminar rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}