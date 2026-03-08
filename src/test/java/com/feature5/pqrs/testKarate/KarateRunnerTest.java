package com.feature5.pqrs.testKarate;

import com.intuit.karate.junit5.Karate;
import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

/**
 * Runner principal de Karate que levanta el contexto completo de Spring Boot
 * en un puerto aleatorio y apunta todos los feature files al servidor activo.
 *
 * <p>Antes de ejecutar los tests se crean dos usuarios de prueba:
 * <ul>
 *   <li><b>karate_admin</b> - rol Administrador (idRol=1)</li>
 *   <li><b>karate_user</b>  - rol Usuario (idRol=3)</li>
 * </ul>
 *
 * Los feature files leen el puerto del system-property {@code karate.baseUrl}
 * que se inyecta desde aquí a través de {@code System.setProperty}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KarateRunnerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioService usuarioService;

    @BeforeAll
    void setup() {
        // Configurar la URL base para que karate-config.js la tome
        System.setProperty("karate.baseUrl", "http://localhost:" + port);

        crearUsuarioSiNoExiste(
                "karate_admin", "Admin1234!", "Karate", "Admin", "karate.admin@test.com", 1);
        crearUsuarioSiNoExiste(
                "karate_user", "Karate123!", "Karate", "User", "karate.user@test.com", 3);
    }

    // ------------------------------------------------------------------
    // Tests de Karate – uno por módulo para facilitar el reporte
    // ------------------------------------------------------------------

    @Karate.Test
    Karate testAuth() {
        return Karate.run("classpath:karate/auth.feature");
    }

    @Karate.Test
    Karate testUsuarios() {
        return Karate.run("classpath:karate/usuarios.feature");
    }

    @Karate.Test
    Karate testRoles() {
        return Karate.run("classpath:karate/roles.feature");
    }

    @Karate.Test
    Karate testEstados() {
        return Karate.run("classpath:karate/estados.feature");
    }

    @Karate.Test
    Karate testTipos() {
        return Karate.run("classpath:karate/tipos.feature");
    }

    @Karate.Test
    Karate testPqrs() {
        return Karate.run("classpath:karate/pqrs.feature");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void crearUsuarioSiNoExiste(String nickname, String password,
                                        String nombre, String apellido,
                                        String email, int idRol) {
        if (usuarioService.buscarPorNickname(nickname) == null) {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNickname(nickname);
            dto.setPassword(password);
            dto.setNombre(nombre);
            dto.setApellido(apellido);
            dto.setEmail(email);

            RolDTO rolDTO = new RolDTO();
            rolDTO.setIdRol(idRol);
            dto.setRol(rolDTO);

            usuarioService.registrarUsuarioWithRole(dto, idRol);
        }
    }
}
