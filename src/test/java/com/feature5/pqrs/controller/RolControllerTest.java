package com.feature5.pqrs.controller;

import com.feature5.pqrs.entities.Rol;
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
    // eliminar usuarios primero para evitar violaciones de FK (usuario.idrol -> rol.idrol)
    usuarioRepository.deleteAll();
    rolRepository.deleteAll();

        Rol r = new Rol();
        r.setDescripcion("Admin role");

        Rol creado = rolController.crearRol(r);
        assertNotNull(creado);
        assertNotNull(creado.getIdRol());

        Long id = creado.getIdRol();

        assertEquals("Admin role", rolController.obtenerRolPorId(id).getBody().getDescripcion());

        Rol actualizado = new Rol();
        actualizado.setDescripcion("User role");

        assertEquals(200, rolController.actualizarRol(id, actualizado).getStatusCodeValue());
        assertEquals("User role", rolRepository.findById(id).get().getDescripcion());

        List<Rol> all = rolController.listarRoles();
        assertFalse(all.isEmpty());

        assertEquals(200, rolController.eliminarRol(id).getStatusCodeValue());
        assertFalse(rolRepository.existsById(id));
    }
}
