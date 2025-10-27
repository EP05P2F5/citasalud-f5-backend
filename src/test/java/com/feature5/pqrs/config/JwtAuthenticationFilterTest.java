package com.feature5.pqrs.config;

import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void publicEndpointIsBypassed() throws Exception {
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk());
    }

    @Test
    void validBearerTokenAllowsAccess() throws Exception {
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_USER");
        rol = rolRepository.save(rol);

        Usuario u = new Usuario();
        u.setNombre("Admin");
        u.setApellido("User");
        u.setNickname("admin");
        u.setPassword(passwordEncoder.encode("password"));
        u.setEmail("admin@example.com");
        u.setRol(rol);
        usuarioRepository.save(u);

        String token = jwtUtils.generateToken("admin");

        // Test con endpoint que existe
        mockMvc.perform(get("/api/test/public")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
