package com.feature5.pqrs.service;

import com.feature5.pqrs.entities.*;
import com.feature5.pqrs.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Descripción de prueba");

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertNotNull(resultado);
        assertEquals("Descripción de prueba", resultado.getDescripcion());
        assertEquals(usuario.getIdUsuario(), resultado.getUsuario().getIdUsuario());
        assertEquals(tipo.getIdTipo(), resultado.getTipo().getIdTipo());
        assertEquals(estadoPendiente.getIdEstado(), resultado.getEstado().getIdEstado());
    }

    @Test
    void testCreatePqrs_LogicaRadicadoYEstadoTexto() {
        // Caso 1: Radicado custom debe respetarse
        Pqrs pqrs1 = new Pqrs();
        pqrs1.setUsuario(usuario);
        pqrs1.setTipo(tipo);
        pqrs1.setEstado(estadoPendiente);
        pqrs1.setDescripcion("Test");
        pqrs1.setRadicado("R-CUSTOM-123");
        pqrs1.setEstadoTexto("ESTADO_CUSTOM");

        Pqrs resultado1 = pqrsService.createPqrs(pqrs1);
        assertEquals("R-CUSTOM-123", resultado1.getRadicado());
        assertEquals("ESTADO_CUSTOM", resultado1.getEstadoTexto());

        // Caso 2: Si no hay radicado ni estadoTexto, deben mantenerse nulos
        Pqrs pqrs2 = new Pqrs();
        pqrs2.setUsuario(usuario);
        pqrs2.setTipo(tipo);
        pqrs2.setEstado(estadoPendiente);
        pqrs2.setDescripcion("Test sin radicado");

        Pqrs resultado2 = pqrsService.createPqrs(pqrs2);

        assertNull(resultado2.getRadicado());
        assertNull(resultado2.getEstadoTexto());
    }

    @Test
    void testCreatePqrs_ConFechaPreexistente_DebeRespetarla() {
        LocalDateTime fechaAnterior = LocalDateTime.now().minusDays(5);

        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Test");
        pqrs.setFechaDeGeneracion(fechaAnterior);

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertEquals(fechaAnterior, resultado.getFechaDeGeneracion());
    }

    @Test
    void testResponderPqrs_DebeActualizarRespuestaYEstado() {
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("PQRS para responder");
        pqrs = pqrsService.createPqrs(pqrs);

        String respuesta = "Esta es la respuesta";
        Optional<Pqrs> resultado = pqrsService.responderPqrs(pqrs.getIdPqrs(), respuesta);

        assertTrue(resultado.isPresent());
        assertEquals(respuesta, resultado.get().getRespuesta());
        assertNotNull(resultado.get().getFechaDeRespuesta());
        assertEquals("RESPONDIDO", resultado.get().getEstadoTexto());
        assertEquals(estadoRespondido.getIdEstado(), resultado.get().getEstado().getIdEstado());
    }

    @Test
    void testResponderPqrs_ConIdInexistente_DebeRetornarEmpty() {
        Optional<Pqrs> resultado = pqrsService.responderPqrs(99999L, "Respuesta");
        assertTrue(resultado.isEmpty());
    }
}
