package com.feature5.pqrs.controller;

import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.PqrsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PqrsControllerTest {

    @Autowired
    private PqrsController pqrsController;

    @Autowired
    private PqrsRepository pqrsRepository;

    @BeforeEach
    void setup() {
        pqrsRepository.deleteAll();
    }

    @Test
    void postReturnsBadRequestWhenRequiredMissing() {
        Pqrs pqrs = new Pqrs();
        pqrs.setDescripcion("sin usuario ni tipo");

        try {
            pqrsController.crearPqrs(pqrs);
            fail("Expected ResponseStatusException for missing fields");
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getStatusCode());
        }
    }

    @Test
    void createAndGetFlow() {
        Pqrs pqrs = new Pqrs();
        pqrs.setIdUsuario(1L);
        pqrs.setIdTipo(1);
        pqrs.setDescripcion("Descripcion test");
        pqrs.setFechaDeGeneracion(LocalDate.now());
        pqrs.setRadicado("R-123");

        org.springframework.http.ResponseEntity<Pqrs> createdResp = pqrsController.crearPqrs(pqrs);
        assertEquals(201, createdResp.getStatusCodeValue());
        Pqrs created = createdResp.getBody();
        assertNotNull(created);
        Long id = created.getIdPqrs();

        org.springframework.http.ResponseEntity<Pqrs> fetched = pqrsController.obtenerPqrsPorId(id);
    assertEquals(200, fetched.getStatusCodeValue());
    assertNotNull(fetched.getBody());
    assertEquals("Descripcion test", fetched.getBody().getDescripcion());
    }

    @Test
    void putAndDeleteFlows() {
        Pqrs base = new Pqrs();
        base.setIdUsuario(1L);
        base.setIdTipo(1);
        base.setDescripcion("orig");
        base.setFechaDeGeneracion(LocalDate.now());
        base.setRadicado("R-50");

        Pqrs saved = pqrsRepository.save(base);
        Long id = saved.getIdPqrs();

        Pqrs update = new Pqrs();
        update.setIdUsuario(2L);
    update.setIdTipo(2);
        update.setDescripcion("Modificado");

        org.springframework.http.ResponseEntity<Pqrs> updated = pqrsController.actualizarPqrs(id, update);
        assertEquals(200, updated.getStatusCodeValue());

        org.springframework.http.ResponseEntity<?> deleted = pqrsController.eliminarPqrs(id);
        assertEquals(200, deleted.getStatusCodeValue());
        assertFalse(pqrsRepository.existsById(id));
    }
}
