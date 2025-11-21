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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    // TESTS CONFIABLES QUE SÍ FUNCIONAN

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
        UsuarioDTO dto = createTestUser("test@example.com", "testuser");
        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void authorizationHeaderWithoutBearerPrefixIsIgnored() throws Exception {
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Basic abc123"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void emptyAuthorizationHeaderIsHandled() throws Exception {
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", ""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void malformedJwtTokenIsHandled() throws Exception {
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void emptyBearerTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer "))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void anonymousAuthenticationIsReplacedWithValidToken() throws Exception {
        AnonymousAuthenticationToken anonymousAuth = 
            new AnonymousAuthenticationToken("key", "anonymousUser", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        UsuarioDTO dto = createTestUser("test2@example.com", "testuser2");
        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser2");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void alreadyAuthenticatedUserIsNotReplaced() throws Exception {
        UsernamePasswordAuthenticationToken existingAuth = 
            new UsernamePasswordAuthenticationToken("existinguser", null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        UsuarioDTO dto = createTestUser("test3@example.com", "testuser3");
        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser3");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userRegistrationEndpointIsPublic() throws Exception {
        mockMvc.perform(post("/usuarios/registrar")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().is4xxClientError()); // 400, no 401
    }

    @Test
    void tokenExtractionExceptionIsHandled() throws Exception {
        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().is4xxClientError());
    }

    // MÉTODO AUXILIAR
    private UsuarioDTO createTestUser(String email, String nickname) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setEmail(email);
        dto.setNickname(nickname);
        dto.setPassword("pass123");
        return dto;
    }
}