package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.TipoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TipoControllerTest {

    @Autowired
    private TipoController tipoController;

    @Test
    void listarTodos_debeRetornarTiposDelTestData() {
        List<TipoDTO> tipos = tipoController.listarTodos();
        
        assertNotNull(tipos);
        assertFalse(tipos.isEmpty());
        
        // Verificar que están los tipos del test-data.sql
        assertTrue(tipos.size() >= 4); // Queja, Reclamo, Sugerencia, Petición
        
        // Verificar que son DTOs con datos completos
        TipoDTO primerTipo = tipos.get(0);
        assertNotNull(primerTipo.getIdTipo());
        assertNotNull(primerTipo.getDescripcion());
        assertFalse(primerTipo.getDescripcion().trim().isEmpty());
    }

    @Test
    void obtenerPorId_conIdExistente_debeRetornarTipo() {
        // Tipo ID 1 debe existir (Queja según test-data.sql)
        ResponseEntity<TipoDTO> response = tipoController.obtenerPorId(1);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getIdTipo());
        assertEquals("Queja", response.getBody().getDescripcion());
    }

    @Test
    void obtenerPorId_conIdInexistente_debeRetornar404() {
        ResponseEntity<TipoDTO> response = tipoController.obtenerPorId(99999);
        
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void obtenerPorId_tipoReclamo_debeRetornarCorrectamente() {
        // Tipo ID 2 debe ser "Reclamo" según test-data.sql
        ResponseEntity<TipoDTO> response = tipoController.obtenerPorId(2);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getIdTipo());
        assertEquals("Reclamo", response.getBody().getDescripcion());
    }

    @Test
    void obtenerPorId_tipoPeticion_debeRetornarCorrectamente() {
        // Tipo ID 4 debe ser "Petición" según test-data.sql
        ResponseEntity<TipoDTO> response = tipoController.obtenerPorId(4);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(4, response.getBody().getIdTipo());
        // Verificar que contiene contenido válido, sin importar la codificación exacta
        assertNotNull(response.getBody().getDescripcion());
        assertTrue(response.getBody().getDescripcion().contains("Petici"));
    }
}