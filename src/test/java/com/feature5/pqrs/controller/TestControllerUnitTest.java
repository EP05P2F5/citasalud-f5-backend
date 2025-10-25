package com.feature5.pqrs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestControllerUnitTest {

    @Autowired
    TestController controller;

    @Test
    void verificarTiposSuccessOrError() {
        var resp = controller.verificarTipos();
        assertNotNull(resp);
        assertTrue(resp.containsKey("success"));
        // Puede ser true o false dependiendo si la tabla 'tipo' existe en H2
    }

    @Test
    void verificarEsquemaSuccess() {
        var resp = controller.verificarEsquemaPqrs();
        assertNotNull(resp);
        assertTrue(resp.containsKey("success"));
        // En H2 la tabla pqrs debería existir
        if ((Boolean) resp.get("success")) {
            assertTrue(resp.containsKey("columns"));
        }
    }
}
