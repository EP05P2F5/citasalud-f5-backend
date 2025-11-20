package com.feature5.pqrs.controller;

import com.feature5.pqrs.config.JwtUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    public void publicEndpoint_returnsSuccess() throws Exception {
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(2)
    public void secureEndpoint_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test/seguro"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    public void jwtFilter_nonBearerHeader_allowsButNoAuth() throws Exception {
        mockMvc.perform(get("/api/test/seguro").header("Authorization", "Token abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    public void registrarUser_then_secureEndpoint_withValidJwt_authenticates() throws Exception {
        String userJson = "{\"nombre\":\"Tc\",\"apellido\":\"User\",\"email\":\"tc.user@example.com\",\"nickname\":\"tc_user\",\"password\":\"pass1234\",\"rol\":{\"idRol\":3}}";

        // Ensure role id 3 exists (test DB may be empty)
        jdbcTemplate.update("MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (?, ?)", 3, "Usuario");

        mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        // Generate token and call secure endpoint
        String token = jwtUtils.generateToken("tc_user");

        mockMvc.perform(get("/api/test/seguro").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticatedUser").value("tc_user"));
    }

    @Test
    @Order(5)
    public void envEndpoint_returnsLocalByDefault() throws Exception {
        mockMvc.perform(get("/api/test/env"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value("Local"));
    }
}
