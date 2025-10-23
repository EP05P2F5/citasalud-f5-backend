package com.feature5.pqrs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/test/public")
    public String publicEndpoint() {
        return "✅ Endpoint público accesible sin token";
    }

    @GetMapping("/api/test/secure")
    public String secureEndpoint() {
        return "🔒 Endpoint seguro: token válido";
    }

    @GetMapping("/api/test/tipos")
    public Map<String, Object> verificarTipos() {
        try {
            List<Map<String, Object>> tipos = jdbcTemplate.queryForList("SELECT * FROM tipo LIMIT 10");
            return Map.of("success", true, "tipos", tipos);
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    @GetMapping("/api/test/schema")
    public Map<String, Object> verificarEsquemaPqrs() {
        try {
            String query = "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name = 'pqrs' ORDER BY ordinal_position";
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(query);
            return Map.of("success", true, "columns", columns);
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}


