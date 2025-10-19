package com.feature5.pqrs.controller;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.PqrsRepository;

@SpringBootTest
class PqrsControllerTest {

    @Autowired
    PqrsController pqrsController;

    @Autowired
    PqrsRepository pqrsRepository;

    @Test
    void crudAndSearch() {
        // start clean
        pqrsRepository.deleteAll();

        Pqrs p = new Pqrs();
        p.setIdUsuario(1L);
        p.setIdTipo("QUEJA");
        p.setDescripcion("Descripcion test");
        p.setFechaDeGeneracion(LocalDate.now());
        p.setRadicado("R-123");
        p.setEstado("ABIERTO");

        // create
        Pqrs creado = pqrsController.crearPqrs(p);
        assertNotNull(creado);
        assertNotNull(creado.getIdPqrs());

        Long id = creado.getIdPqrs();

        // read
        assertTrue(pqrsController.obtenerPqrsPorId(id).getBody().getDescripcion().contains("Descripcion test"));

        // update
        Pqrs actualizado = new Pqrs();
        actualizado.setIdUsuario(2L);
        actualizado.setIdTipo("PETICION");
        actualizado.setDescripcion("Modificado");
        actualizado.setFechaDeGeneracion(LocalDate.now());
        actualizado.setRadicado("R-999");
        actualizado.setEstado("CERRADO");

        assertEquals(200, pqrsController.actualizarPqrs(id, actualizado).getStatusCodeValue());
        assertEquals("CERRADO", pqrsRepository.findById(id).get().getEstado());

        // buscar por estado
        List<Pqrs> encontrados = pqrsController.buscarPorEstado("CERRADO");
        assertFalse(encontrados.isEmpty());

        // buscar por usuario
        List<Pqrs> porUsuario = pqrsController.buscarPorUsuario(2L);
        assertFalse(porUsuario.isEmpty());

        // listar todos
        List<Pqrs> todos = pqrsController.listarPqrs();
        assertFalse(todos.isEmpty());

        // delete
        assertEquals(200, pqrsController.eliminarPqrs(id).getStatusCodeValue());
        assertFalse(pqrsRepository.existsById(id));
    }
}
