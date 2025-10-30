package com.feature5.pqrs.controller;

import com.feature5.pqrs.constants.ResponseKeys;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.feature5.pqrs.constants.ResponseKeys.*;

@RestController
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/test/tipos")
    public Map<String, Object> verificarTipos() {
        try {
            List<Map<String, Object>> tipos = jdbcTemplate.queryForList("SELECT * FROM tipo LIMIT 10");
            return Map.of(
                    SUCCESS, true,
                    "tipos", tipos
            );
        } catch (Exception e) {
            return Map.of(
                    SUCCESS, false,
                    ERROR, e.getMessage()
            );
        }
    }

    @GetMapping("/api/test/schema")
    public Map<String, Object> verificarEsquemaPqrs() {
        try {
            String query = "SELECT column_name, data_type, is_nullable " +
                    "FROM information_schema.columns " +
                    "WHERE table_name = 'pqrs' ORDER BY ordinal_position";
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(query);
            return Map.of(
                    SUCCESS, true,
                    "columns", columns
            );
        } catch (Exception e) {
            return Map.of(
                    SUCCESS, false,
                    ERROR, e.getMessage()
            );
        }
    }

    @GetMapping("/api/test/env")
    public Map<String, Object> checkEnv() {
        try {
            String render = System.getenv("RENDER");
            String azure = System.getenv("WEBSITE_SITE_NAME");

            String active = render != null ? "Render" :
                    azure != null ? "Azure" : "Local";

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("render", render != null ? render : "null");
            response.put("azure", azure != null ? azure : "null");
            response.put("active", active);

            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            return error;
        }
    }


    @GetMapping("/api/test/seguro")
    public Map<String, Object> verificarAccesoSeguro() {
        try {
            // Obtener el usuario autenticado desde el contexto de seguridad
            var authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();

            String username = (authentication != null && authentication.isAuthenticated())
                    ? authentication.getName()
                    : "Usuario desconocido";

            return Map.of(
                    SUCCESS, true,
                    "message", "Acceso exitoso al sitio seguro. El token JWT fue validado correctamente.",
                    "authenticatedUser", username
            );
        } catch (Exception e) {
            return Map.of(
                    SUCCESS, false,
                    ERROR, e.getMessage()
            );
        }
    }



    @GetMapping("/api/test/public")
    public Map<String, Object> publicEndpoint() {
        return Map.of(
                SUCCESS, true,
                "message", "Este endpoint es público y no requiere autenticación."
        );
    }


}
