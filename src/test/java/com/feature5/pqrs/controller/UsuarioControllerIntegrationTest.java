package com.feature5.pqrs.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

        @Autowired
        private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    public void registrarPublic_and_list_containsUser_and_login() throws Exception {
        String nickname = "uc_" + UUID.randomUUID().toString().substring(0, 8);
        String userJson = String.format("{\"nombre\":\"Uc\",\"apellido\":\"User\",\"email\":\"%s@example.com\",\"nickname\":\"%s\",\"password\":\"pass1234\",\"rol\":{\"idRol\":3}}", nickname, nickname);

        // Ensure role id 3 exists for public registration
        jdbcTemplate.update("MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (?, ?)", 3, "Usuario");

        mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        // login and obtain token
        String loginJson = String.format("{\"nickname\":\"%s\",\"password\":\"pass1234\"}", nickname);
        var mvcResult = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(nickname))
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn();

        // extract token
        String json = mvcResult.getResponse().getContentAsString();
        com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
        String token = node.get("token").asText();

        // listar con token y comprobar que aparece el nickname
        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..nickname", Matchers.hasItem(nickname)));
    }
}
