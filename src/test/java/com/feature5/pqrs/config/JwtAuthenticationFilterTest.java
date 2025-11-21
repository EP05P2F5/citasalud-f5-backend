package com.feature5.pqrs.config;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests básicos del filtro JWT.
 * El filtro se ejecuta en el contexto de Spring Security.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JwtAuthenticationFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    // Tests existentes que funcionan
    @Test
    void publicEndpointIsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/test/seguro"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void authenticatedUserCanAccessProtectedEndpoint() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setEmail("test@example.com");
        dto.setNickname("testuser");
        dto.setPassword("pass123");

        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer malformed"))
                .andExpect(status().is4xxClientError());
    }

    // SOLO 3 TESTS NUEVOS PARA CUBRIR IF Y WHILE
    @Test
    void authorizationHeaderWithoutBearerPrefixIsIgnored() throws Exception {
        // Cubre el if que verifica si el header no empieza con "Bearer "
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Basic abc123"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void anonymousAuthenticationIsReplacedWithValidToken() throws Exception {
        // Cubre el if que verifica si la autenticación actual es anónima
        AnonymousAuthenticationToken anonymousAuth = 
            new AnonymousAuthenticationToken("key", "anonymousUser", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setEmail("test2@example.com");
        dto.setNickname("testuser2");
        dto.setPassword("pass123");
        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser2");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void alreadyAuthenticatedUserIsNotReplaced() throws Exception {
        // Cubre el if que verifica si ya hay autenticación no-anónima
        UsernamePasswordAuthenticationToken existingAuth = 
            new UsernamePasswordAuthenticationToken("existinguser", null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setEmail("test3@example.com");
        dto.setNickname("testuser3");
        dto.setPassword("pass123");
        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser3");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}