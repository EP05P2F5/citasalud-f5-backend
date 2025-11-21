package com.feature5.pqrs.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests completos para endpoints de testing y verificación del sistema
 */
@SpringBootTest
class TestControllerTest {

    @Autowired
    private TestController testController;
    
    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    // Tests existentes (ya funcionan bien)
    
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
    
    @Test
    void verificarAccesoSeguro_sinAutenticacion_retornaUsuarioDesconocido() {
        SecurityContextHolder.clearContext();
        
        Map<String, Object> response = testController.verificarAccesoSeguro();
        
        assertTrue((Boolean) response.get("success"));
        assertEquals("Usuario desconocido", response.get("authenticatedUser"));
    }
    
    @Test
    void verificarAccesoSeguro_conAutenticacion_retornaUsername() {
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken("testuser", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Map<String, Object> response = testController.verificarAccesoSeguro();
        
        assertTrue((Boolean) response.get("success"));
        assertEquals("testuser", response.get("authenticatedUser"));
    }
    
    @Test
    void verificarAccesoSeguro_conAutenticacionNoAutenticada_retornaUsuarioDesconocido() {
        // Crear una autenticación no autenticada
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken("testuser", null, java.util.Collections.emptyList());
        auth.setAuthenticated(false); // Explicitamente no autenticado
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Map<String, Object> response = testController.verificarAccesoSeguro();
        
        assertTrue((Boolean) response.get("success"));
        assertEquals("Usuario desconocido", response.get("authenticatedUser"));
    }

    @Test
    void checkEnv_conVariablesEntornoEspecificas() {
        // Este test depende del entorno actual, pero valida el comportamiento
        Map<String, Object> response = testController.checkEnv();
        
        assertNotNull(response);
        assertTrue((Boolean) response.get("success"));
        
        // Validar que los valores de render y azure son strings (null o valor)
        assertTrue(response.get("render") instanceof String);
        assertTrue(response.get("azure") instanceof String);
    }

    @Test
    void publicEndpoint_estructuraCorrecta() {
        Map<String, Object> response = testController.publicEndpoint();
        
        assertEquals(2, response.size());
        assertTrue((Boolean) response.get("success"));
        assertEquals("Este endpoint es público y no requiere autenticación.", response.get("message"));
    }

    @Test
    void verificarTipos_estructuraCorrecta() {
        Map<String, Object> response = testController.verificarTipos();
        
        assertTrue(response.containsKey("success"));
        assertTrue(response.containsKey("tipos"));
        // 'tipos' debería ser una lista
        assertTrue(response.get("tipos") instanceof java.util.List);
    }

    @Test
    void verificarEsquemaPqrs_estructuraCorrecta() {
        Map<String, Object> response = testController.verificarEsquemaPqrs();
        
        assertTrue(response.containsKey("success"));
        assertTrue(response.containsKey("columns"));
        // 'columns' debería ser una lista
        assertTrue(response.get("columns") instanceof java.util.List);
    }

    // Tests para validar el manejo de errores en SecurityContext
    @Test
    void verificarAccesoSeguro_conAuthenticationNula() {
        SecurityContextHolder.getContext().setAuthentication(null);
        
        Map<String, Object> response = testController.verificarAccesoSeguro();
        
        assertTrue((Boolean) response.get("success"));
        assertEquals("Usuario desconocido", response.get("authenticatedUser"));
    }

    // Test para cubrir branches de condiciones en checkEnv
    @Test
    void checkEnv_validaTodosLosCampos() {
        Map<String, Object> response = testController.checkEnv();
        
        // Validar que todos los campos esperados existen
        String[] expectedKeys = {"success", "active", "render", "azure"};
        for (String key : expectedKeys) {
            assertTrue(response.containsKey(key), "Debe contener la key: " + key);
        }
        
        // Validar tipos de datos
        assertTrue(response.get("success") instanceof Boolean);
        assertTrue(response.get("active") instanceof String);
        assertTrue(response.get("render") instanceof String);
        assertTrue(response.get("azure") instanceof String);
    }

    // Test para verificar la estructura de respuesta de endpoints seguros
    @Test
    void verificarAccesoSeguro_estructuraCompleta() {
        // Configurar autenticación
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken("usuarioPrueba", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Map<String, Object> response = testController.verificarAccesoSeguro();
        
        // Validar estructura completa
        assertEquals(3, response.size());
        assertTrue((Boolean) response.get("success"));
        assertTrue(response.get("message") instanceof String);
        assertEquals("usuarioPrueba", response.get("authenticatedUser"));
    }
}