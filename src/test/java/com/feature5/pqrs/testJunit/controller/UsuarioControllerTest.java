package com.feature5.pqrs.testJunit.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.controller.AuthController;
import com.feature5.pqrs.controller.UsuarioController;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.feature5.pqrs.DTO.RolDTO;

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

    @Test
    void registrarAutenticadoListarGestoresActualizarEliminarYBuscarPorId() {
        // Registrar un usuario con rol Gestor (id = 2) usando el endpoint autenticado
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Gestor");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1985, 5, 5));
        dto.setDireccion("Calle Gestor");
        dto.setEmail("gestor@example.com");
        dto.setTelefono("99999");
        dto.setNickname("gestornick");
        dto.setPassword("gestorpass");
        dto.setRol(new RolDTO(2, "Gestor"));

        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "N/A", List.of());

        ResponseEntity<UsuarioDTO> created = usuarioController.registrar(dto, auth);
        assertEquals(201, created.getStatusCode().value());
        assertNotNull(created.getBody());
        Long createdId = created.getBody().getIdUsuario();
        assertNotNull(createdId);
        assertEquals(2, created.getBody().getRol().getIdRol());

        // Listar gestores debe contener al menos el creado
        ResponseEntity<List<UsuarioDTO>> gestores = usuarioController.listarGestores();
        assertTrue(gestores.getBody().stream().anyMatch(u -> "gestornick".equals(u.getNickname())));

        // Buscar por ID
        ResponseEntity<UsuarioDTO> found = usuarioController.buscarPorId(createdId);
        assertEquals(200, found.getStatusCode().value());

        // Actualizar nombre
        UsuarioDTO updateDto = new UsuarioDTO();
        updateDto.setNombre("GestorMod");
        ResponseEntity<UsuarioDTO> updated = usuarioController.actualizarPorId(createdId, updateDto);
        assertEquals(200, updated.getStatusCode().value());
        assertEquals("GestorMod", updated.getBody().getNombre());

        // Intento de cambiar nickname (debe dar 400)
        UsuarioDTO badDto = new UsuarioDTO();
        badDto.setNickname("otro");
        ResponseEntity<UsuarioDTO> badUpdate = usuarioController.actualizarPorId(createdId, badDto);
        assertEquals(400, badUpdate.getStatusCode().value());

        // Eliminar
        ResponseEntity<Void> deleted = usuarioController.eliminarPorId(createdId);
        assertEquals(204, deleted.getStatusCode().value());

        // Buscar debe retornar 404
        ResponseEntity<UsuarioDTO> notFound = usuarioController.buscarPorId(createdId);
        assertEquals(404, notFound.getStatusCode().value());
    }
}
