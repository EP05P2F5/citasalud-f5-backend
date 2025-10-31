package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RolControllerTest {

    @Autowired
    RolController rolController;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    void crudRole() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        RolDTO r = new RolDTO();
        r.setDescripcion("Admin role");

        RolDTO creado = rolController.crearRol(r);
        assertNotNull(creado);
        assertNotNull(creado.getIdRol());

        Long id = creado.getIdRol();

        assertEquals("Admin role", rolController.obtenerRolPorId(id).getBody().getDescripcion());

        RolDTO actualizado = new RolDTO();
        actualizado.setDescripcion("User role");

        assertEquals(200, rolController.actualizarRol(id, actualizado).getStatusCodeValue());
        assertEquals("User role", rolRepository.findById(id).get().getDescripcion());

        List<RolDTO> all = rolController.listarRoles();
        assertFalse(all.isEmpty());

        assertEquals(200, rolController.eliminarRol(id).getStatusCodeValue());
        assertFalse(rolRepository.existsById(id));
    }

    @Test
    void testErroresBasicos() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        // 404s: obtener, actualizar, eliminar inexistente
        assertEquals(404, rolController.obtenerRolPorId(99999L).getStatusCodeValue());
        
        RolDTO rolInexistente = new RolDTO();
        rolInexistente.setDescripcion("Inexistente");
        assertEquals(404, rolController.actualizarRol(99999L, rolInexistente).getStatusCodeValue());
        assertEquals(404, rolController.eliminarRol(99999L).getStatusCodeValue());
    }
}
