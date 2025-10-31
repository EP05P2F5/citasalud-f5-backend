package com.feature5.pqrs.config;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests básicos del filtro JWT.
 * El filtro se ejecuta en el contexto de Spring Security.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void publicEndpointIsAccessibleWithoutToken() throws Exception {
        // Endpoint público debe ser accesible sin autenticación
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        // Endpoint protegido sin token debe retornar 401 o 403
        mockMvc.perform(get("/api/test/seguro"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void authenticatedUserCanAccessProtectedEndpoint() throws Exception {
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_USER");
        rol = rolRepository.save(rol);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setEmail("test@example.com");
        dto.setNickname("testuser");
        dto.setPassword("pass123");
        
        RolDTO rolDTO = new RolDTO();
        rolDTO.setIdRol(rol.getIdRol());
        rolDTO.setDescripcion(rol.getDescripcion());
        dto.setRol(rolDTO);

        usuarioService.registrarUsuario(dto);
        String token = jwtUtils.generateToken("testuser");

        mockMvc.perform(get("/api/test/seguro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        // Token malformado debe ser rechazado
        mockMvc.perform(get("/api/test/secure")
                        .header("Authorization", "Bearer malformed"))
                .andExpect(status().is4xxClientError());
    }
}
