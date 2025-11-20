package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba integral del flujo de usuario:
 * Registrar -> Login exitoso -> Login fallido -> Listar -> Buscar.
 */
@SpringBootTest
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UsuarioControllerTest {

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private AuthController authController;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        // No borramos roles porque vienen del script SQL con IDs específicos
    }

    @Test
    void registerLoginListAndFind() {
        // 2️⃣ Crear DTO del nuevo usuario (el rol se ignora, siempre asigna ID 3)
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle 1");
        dto.setEmail("test@example.com");
        dto.setTelefono("123456");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        // No es necesario asignar rol, el sistema asigna automáticamente ID 3

        // 3️⃣ Registrar usuario (endpoint público)
        ResponseEntity<UsuarioDTO> created = usuarioController.registrarPublic(dto);
        assertEquals(201, created.getStatusCode().value(), "El registro del usuario falló");
        assertNotNull(created.getBody(), "El cuerpo de la respuesta no debe ser nulo");
        assertNotNull(created.getBody().getIdUsuario(), "El usuario creado debe tener ID");

        // 4️⃣ Login exitoso (AuthController + JWT)
        ResponseEntity<?> loginOk = authController.login(
                new LoginRequestDTO("testnick", "pass123")
        );
        assertEquals(200, loginOk.getStatusCode().value(), "El login debería ser exitoso");
        assertTrue(loginOk.getBody() instanceof Map, "La respuesta del login debe ser un Map");

        Map<?, ?> okBody = (Map<?, ?>) loginOk.getBody();
        assertTrue(okBody.containsKey("token"), "La respuesta del login debe incluir un token");
        assertEquals("testnick", okBody.get("username"), "El username devuelto debe coincidir");

        // 5️⃣ Login incorrecto (contraseña inválida)
        ResponseEntity<?> loginFail = authController.login(
                new LoginRequestDTO("testnick", "wrong")
        );
        assertEquals(401, loginFail.getStatusCode().value(), "El login con contraseña incorrecta debe devolver 401");

        // 6️⃣ Listar usuarios
        ResponseEntity<List<UsuarioDTO>> list = usuarioController.listar();
        assertEquals(1, list.getBody().size());

        // 7️⃣ Buscar por nickname
        ResponseEntity<UsuarioDTO> found = usuarioController.buscarPorNickname("testnick");
        assertEquals(200, found.getStatusCode().value());

        // 8️⃣ Buscar usuario inexistente debe retornar 404
        ResponseEntity<UsuarioDTO> notFound = usuarioController.buscarPorNickname("usuarioinexistente");
        assertEquals(404, notFound.getStatusCode().value());
    }
}
