package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.EstadoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EstadoControllerTest {

    @Autowired
    private EstadoController estadoController;

    @Test
    void listarTodos_debeRetornarEstadosDelTestData() {
        List<EstadoDTO> estados = estadoController.listarTodos();
        
        assertNotNull(estados);
        assertFalse(estados.isEmpty());
        
        // Verificar que están los estados del test-data.sql
        assertTrue(estados.size() >= 5); // Pendiente, En proceso, Resuelta, Cerrada, Anulada
        
        // Verificar que son DTOs con datos completos
        EstadoDTO primerEstado = estados.get(0);
        assertNotNull(primerEstado.getIdEstado());
        assertNotNull(primerEstado.getDescripcion());
        assertFalse(primerEstado.getDescripcion().trim().isEmpty());
    }

    @Test
    void obtenerPorId_conIdExistente_debeRetornarEstado() {
        // Estado ID 1 debe existir (Pendiente según test-data.sql)
        ResponseEntity<EstadoDTO> response = estadoController.obtenerPorId(1);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getIdEstado());
        assertEquals("Pendiente", response.getBody().getDescripcion());
    }

    @Test
    void obtenerPorId_conIdInexistente_debeRetornar404() {
        ResponseEntity<EstadoDTO> response = estadoController.obtenerPorId(99999);
        
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void obtenerPorId_estadoResuelta_debeRetornarCorrectamente() {
        // Estado ID 3 debe ser "Resuelta" según test-data.sql
        ResponseEntity<EstadoDTO> response = estadoController.obtenerPorId(3);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getIdEstado());
        assertEquals("Resuelta", response.getBody().getDescripcion());
    }
}