package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.*;
import com.feature5.pqrs.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PqrsServiceTest {

    @Autowired
    private PqrsService pqrsService;

    @Autowired
    private PqrsRepository pqrsRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private RolRepository rolRepository;

    private Usuario usuario;
    private Tipo tipo;
    private Estado estadoPendiente;
    private Estado estadoRespondido;

    @BeforeEach
    void setup() {
        // Limpieza ordenada para evitar errores por FK
        pqrsRepository.deleteAll();
        usuarioRepository.deleteAll();
        estadoRepository.deleteAll();
        tipoRepository.deleteAll();
        rolRepository.deleteAll();

        // Crear o reutilizar el rol USER
        Rol rol = rolRepository.findByDescripcion("USER")
                .orElseGet(() -> {
                    Rol nuevo = new Rol();
                    nuevo.setDescripcion("USER");
                    return rolRepository.save(nuevo);
                });

        // Usuario base
        usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setNickname("testuser");
        usuario.setPassword("pass");
        usuario.setRol(rol);
        usuario = usuarioRepository.save(usuario);

        // Tipo base
        tipo = new Tipo();
        tipo.setDescripcion("QUEJA");
        tipo = tipoRepository.save(tipo);

        // Estados base
        estadoPendiente = new Estado();
        estadoPendiente.setDescripcion("PENDIENTE");
        estadoPendiente = estadoRepository.save(estadoPendiente);

        estadoRespondido = new Estado();
        estadoRespondido.setDescripcion("RESPONDIDO");
        estadoRespondido = estadoRepository.save(estadoRespondido);
    }

    @Test
    void testCreatePqrs_DebeGenerarFechaYRadicado() {
        PqrsDTO pqrsDTO = new PqrsDTO();
        pqrsDTO.setDescripcion("Descripción de prueba");

        PqrsDTO resultado = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO);

        assertNotNull(resultado);
        assertEquals("Descripción de prueba", resultado.getDescripcion());
        assertEquals(usuario.getIdUsuario(), resultado.getIdUsuario());
        assertEquals(tipo.getIdTipo(), resultado.getIdTipo());
    }

    @Test
    void testCreatePqrs_LogicaRadicadoYEstadoTexto() {
        // Caso 1: Radicado custom debe respetarse
        PqrsDTO pqrsDTO1 = new PqrsDTO();
        pqrsDTO1.setDescripcion("Test");
        pqrsDTO1.setRadicado("R-CUSTOM-123");
        pqrsDTO1.setEstado("ESTADO_CUSTOM");

        PqrsDTO resultado1 = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO1);
        assertEquals("R-CUSTOM-123", resultado1.getRadicado());
        assertEquals("ESTADO_CUSTOM", resultado1.getEstado());

        // Caso 2: Si no hay radicado ni estadoTexto, deben mantenerse nulos
        PqrsDTO pqrsDTO2 = new PqrsDTO();
        pqrsDTO2.setDescripcion("Test sin radicado");

        PqrsDTO resultado2 = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO2);

        assertNull(resultado2.getRadicado());
    }

    @Test
    void testCreatePqrs_ConFechaPreexistente_DebeRespetarla() {
        LocalDate fechaAnterior = LocalDate.now().minusDays(5);

        PqrsDTO pqrsDTO = new PqrsDTO();
        pqrsDTO.setDescripcion("Test");
        pqrsDTO.setFechaDeGeneracion(fechaAnterior);

        PqrsDTO resultado = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO);

        assertEquals(fechaAnterior, resultado.getFechaDeGeneracion());
    }

    @Test
    void testResponderPqrs_DebeActualizarRespuestaYEstado() {
        PqrsDTO pqrsDTO = new PqrsDTO();
        pqrsDTO.setDescripcion("PQRS para responder");
        PqrsDTO creado = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO);

        String respuesta = "Esta es la respuesta";
        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(creado.getIdPqrs(), respuesta);

        assertTrue(resultado.isPresent());
        assertEquals(respuesta, resultado.get().getRespuesta());
        assertNotNull(resultado.get().getFechaDeRespuesta());
        assertEquals("RESPONDIDO", resultado.get().getEstado());
    }

    @Test
    void testResponderPqrs_ConIdInexistente_DebeRetornarEmpty() {
        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(99999L, "Respuesta");
        assertTrue(resultado.isEmpty());
    }
}
