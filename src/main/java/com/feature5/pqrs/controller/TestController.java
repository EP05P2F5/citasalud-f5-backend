package com.feature5.pqrs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/public")
    public String publicEndpoint() {
        return "✅ Endpoint público accesible sin token";
    }

    @GetMapping("/api/test/secure")
    public String secureEndpoint() {
        return "🔒 Endpoint seguro: token válido";
    }
}


