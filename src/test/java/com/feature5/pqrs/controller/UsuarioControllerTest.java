package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba integral del flujo de usuario:
 * Registrar -> Login exitoso -> Login fallido -> Listar -> Buscar.
 */
@SpringBootTest
class UsuarioControllerTest {

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private AuthController authController;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void registerLoginListAndFind() {
        // 1️⃣ Crear y guardar rol base
        Rol rol = new Rol();
        rol.setDescripcion("USER");
        rol = rolRepository.save(rol);

        // 2️⃣ Crear DTO del nuevo usuario
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setFechaDeNacimiento(LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle 1");
        dto.setEmail("test@example.com");
        dto.setTelefono("123456");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        
        // Crear RolDTO a partir del rol guardado
        RolDTO rolDTO = new RolDTO();
        rolDTO.setIdRol(rol.getIdRol());
        rolDTO.setDescripcion(rol.getDescripcion());
        dto.setRol(rolDTO);

        // 3️⃣ Registrar usuario
        ResponseEntity<UsuarioDTO> created = usuarioController.registrar(dto);
        assertEquals(200, created.getStatusCodeValue(), "El registro del usuario falló");
        assertNotNull(created.getBody(), "El cuerpo de la respuesta no debe ser nulo");
        assertNotNull(created.getBody().getIdUsuario(), "El usuario creado debe tener ID");

        // 4️⃣ Login exitoso (AuthController + JWT)
        ResponseEntity<?> loginOk = authController.login(
                new LoginRequestDTO("testnick", "pass123")
        );
        assertEquals(200, loginOk.getStatusCodeValue(), "El login debería ser exitoso");
        assertTrue(loginOk.getBody() instanceof Map, "La respuesta del login debe ser un Map");

        Map<?, ?> okBody = (Map<?, ?>) loginOk.getBody();
        assertTrue(okBody.containsKey("token"), "La respuesta del login debe incluir un token");
        assertEquals("testnick", okBody.get("username"), "El username devuelto debe coincidir");

        // 5️⃣ Login incorrecto (contraseña inválida)
        ResponseEntity<?> loginFail = authController.login(
                new LoginRequestDTO("testnick", "wrong")
        );
        assertEquals(401, loginFail.getStatusCodeValue(), "El login con contraseña incorrecta debe devolver 401");

        // 6️⃣ Listar usuarios
        ResponseEntity<List<UsuarioDTO>> list = usuarioController.listar();
        assertEquals(1, list.getBody().size());

        // 7️⃣ Buscar por nickname
        ResponseEntity<UsuarioDTO> found = usuarioController.buscarPorNickname("testnick");
        assertEquals(200, found.getStatusCodeValue());

        // 8️⃣ Buscar usuario inexistente debe retornar 404
        ResponseEntity<UsuarioDTO> notFound = usuarioController.buscarPorNickname("usuarioinexistente");
        assertEquals(404, notFound.getStatusCodeValue());
    }
}
