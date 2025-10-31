package com.feature5.pqrs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para endpoints de testing y verificación del sistema
 */
@SpringBootTest
class TestControllerTest {

    @Autowired
    private TestController testController;

    @Test
    void publicEndpoint_success() {
        Map<String, Object> response = testController.publicEndpoint();

        assertNotNull(response);
        assertTrue((Boolean) response.get("success"));
        assertTrue(response.containsKey("message"));
    }

    @Test
    void verificarTipos_success() {
        Map<String, Object> response = testController.verificarTipos();

        assertNotNull(response);
        assertTrue((Boolean) response.get("success"));
        assertTrue(response.containsKey("tipos"));
    }

    @Test
    void verificarEsquemaPqrs_success() {
        Map<String, Object> response = testController.verificarEsquemaPqrs();

        assertNotNull(response);
        assertTrue((Boolean) response.get("success"));
        assertTrue(response.containsKey("columns"));
    }

    @Test
    void checkEnv_success() {
        Map<String, Object> response = testController.checkEnv();

        assertNotNull(response);
        assertTrue((Boolean) response.get("success"));
        assertTrue(response.containsKey("active"));
        assertTrue(response.containsKey("render"));
        assertTrue(response.containsKey("azure"));
        
        // Validar que active sea uno de los valores esperados
        String active = (String) response.get("active");
        assertTrue(active.equals("Local") || active.equals("Render") || active.equals("Azure"));
    }

    @Test
    void verificarAccesoSeguro_success() {
        Map<String, Object> response = testController.verificarAccesoSeguro();

        assertNotNull(response);
        assertTrue((Boolean) response.get("success"));
        assertTrue(response.containsKey("message"));
        assertTrue(response.containsKey("authenticatedUser"));
    }
}
