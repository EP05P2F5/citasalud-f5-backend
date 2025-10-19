package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.controller.UsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private com.feature5.pqrs.repository.UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        // delete users first to avoid FK constraint when deleting roles
        usuarioRepository.deleteAll();
        usuarioRepository.flush();
        rolRepository.deleteAll();
        rolRepository.flush();
    }

    @Test
    void login_success_returnsTokenAndUser() {
        // prepare role and register user via usuarioController
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_USER");
        rol = rolRepository.save(rol);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(java.time.LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle 1");
        dto.setEmail("test@example.com");
        dto.setTelefono("123456");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        dto.setRol(rol);

        ResponseEntity<UsuarioDTO> created = usuarioController.registrar(dto);
        assertEquals(200, created.getStatusCodeValue());

        LoginRequestDTO req = new LoginRequestDTO();
        req.setNickname("testnick");
        req.setPassword("pass123");

        ResponseEntity<?> resp = authController.login(req);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        // body is a map with token and usuario
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> body = (java.util.Map<String, Object>) resp.getBody();
        assertTrue(body.containsKey("token"));
        assertTrue(body.containsKey("usuario"));
    }

    @Test
    void login_failure_returns401() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setNickname("noexist");
        req.setPassword("bad");

        ResponseEntity<?> resp = authController.login(req);
        assertEquals(401, resp.getStatusCodeValue());
    }
}
