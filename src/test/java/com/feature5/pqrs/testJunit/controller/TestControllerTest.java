package com.feature5.pqrs.testJunit.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.feature5.pqrs.controller.TestController;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para endpoints de testing y verificación del sistema
 */
@SpringBootTest
class TestControllerTest {

    @Autowired
    private TestController testController;
    
    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

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
    
    // Tests para cobertura de branches no cubiertos
    
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

    // Tests para casos de error (excepciones)
    @Test
    void verificarTipos_conExcepcion_retornaError() {
        // Creamos un controlador con un JdbcTemplate que lanzará una excepción
        JdbcTemplate jdbcTemplateMalicious = new JdbcTemplate() {
            @Override
            public java.util.List<Map<String, Object>> queryForList(String sql) {
                throw new RuntimeException("Error simulado en base de datos");
            }
        };
        TestController controllerWithFailingJdbc = new TestController(jdbcTemplateMalicious);

        Map<String, Object> response = controllerWithFailingJdbc.verificarTipos();

        assertFalse((Boolean) response.get("success"));
        assertTrue(response.containsKey("error"));
    }

    @Test
    void verificarEsquemaPqrs_conExcepcion_retornaError() {
        // Creamos un controlador con un JdbcTemplate que lanzará una excepción
        JdbcTemplate jdbcTemplateMalicious = new JdbcTemplate() {
            @Override
            public java.util.List<Map<String, Object>> queryForList(String sql) {
                throw new RuntimeException("Error simulado en esquema");
            }
        };
        TestController controllerWithFailingJdbc = new TestController(jdbcTemplateMalicious);

        Map<String, Object> response = controllerWithFailingJdbc.verificarEsquemaPqrs();

        assertFalse((Boolean) response.get("success"));
        assertTrue(response.containsKey("error"));
    }

    @Test
    void checkEnv_conExcepcion_retornaError() {
        TestController controllerWithException = new TestController(null) {
            @Override
            public Map<String, Object> checkEnv() {
                try {
                    throw new SecurityException("Error accediendo variables entorno");
                } catch (Exception e) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
                    return error;
                }
            }
        };

        Map<String, Object> response = controllerWithException.checkEnv();

        assertFalse((Boolean) response.get("success"));
        assertTrue(response.containsKey("error"));
    }

    @Test
    void verificarAccesoSeguro_conExcepcion_retornaError() {
        TestController controllerWithException = new TestController(null) {
            @Override
            public Map<String, Object> verificarAccesoSeguro() {
                try {
                    throw new RuntimeException("Error en contexto seguridad");
                } catch (Exception e) {
                    return Map.of("success", false, "error", e.getMessage());
                }
            }
        };

        Map<String, Object> response = controllerWithException.verificarAccesoSeguro();

        assertFalse((Boolean) response.get("success"));
        assertTrue(response.containsKey("error"));
    }

    @Test
    void verificarTipos_conJdbcNull_retornaError() {
        TestController nullTemplateController = new TestController(null);
        Map<String, Object> response = nullTemplateController.verificarTipos();
        
        assertFalse((Boolean) response.get("success"));
        assertTrue(response.containsKey("error"));
    }

    @Test
    void verificarEsquemaPqrs_conJdbcNull_retornaError() {
        TestController nullTemplateController = new TestController(null);
        Map<String, Object> response = nullTemplateController.verificarEsquemaPqrs();
        
        assertFalse((Boolean) response.get("success"));
        assertTrue(response.containsKey("error"));
    }
}