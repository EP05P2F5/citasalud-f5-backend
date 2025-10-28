package com.feature5.pqrs.service;

import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.PqrsRepository;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.TipoRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
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
        pqrsRepository.deleteAll();
        usuarioRepository.deleteAll();
        tipoRepository.deleteAll();
        estadoRepository.deleteAll();
        rolRepository.deleteAll();

        Rol rol = new Rol();
        rol.setDescripcion("USER");
        rol = rolRepository.save(rol);

        usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setNickname("testuser");
        usuario.setPassword("pass");
        usuario.setRol(rol);
        usuario = usuarioRepository.save(usuario);

        tipo = new Tipo();
        tipo.setDescripcion("QUEJA");
        tipo = tipoRepository.save(tipo);

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
        assertNotNull(resultado.getFechaDeGeneracion());
        assertNotNull(resultado.getRadicado());
        assertTrue(resultado.getRadicado().startsWith("R-"));
        assertEquals("PENDIENTE", resultado.getEstadoTexto());
    }

    @Test
    void testCreatePqrs_ConRadicadoPreexistente_DebeRespetarlo() {
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Test");
        pqrs.setRadicado("R-CUSTOM-123");

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertEquals("R-CUSTOM-123", resultado.getRadicado());
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

    @Test
    void testCreatePqrs_SinEstadoTexto_DebeTomarloDelEstado() {
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Test sin estado texto");
        pqrs.setEstadoTexto(null);

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertEquals("PENDIENTE", resultado.getEstadoTexto());
    }

    @Test
    void testCreatePqrs_ConEstadoTextoVacio_DebeTomarloDelEstado() {
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Test");
        pqrs.setEstadoTexto("   ");

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertEquals("PENDIENTE", resultado.getEstadoTexto());
    }

    @Test
    void testResponderPqrs_SinEstadoRespondidoEnBD_DebeSoloActualizarTexto() {
        // Crear PQRS
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("PQRS para test sin estado RESPONDIDO");
        pqrs = pqrsService.createPqrs(pqrs);

        // Eliminar el estado RESPONDIDO de la BD
        estadoRepository.delete(estadoRespondido);

        String respuesta = "Respuesta cuando no existe estado RESPONDIDO";
        Optional<Pqrs> resultado = pqrsService.responderPqrs(pqrs.getIdPqrs(), respuesta);

        assertTrue(resultado.isPresent());
        assertEquals(respuesta, resultado.get().getRespuesta());
        assertNotNull(resultado.get().getFechaDeRespuesta());
        assertEquals("RESPONDIDO", resultado.get().getEstadoTexto());
        // El estado sigue siendo PENDIENTE porque no encontró RESPONDIDO en BD
        assertEquals(estadoPendiente.getIdEstado(), resultado.get().getEstado().getIdEstado());
    }

    @Test
    void testCreatePqrs_ConRadicadoVacio_DebeGenerarRadicado() {
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Test");
        pqrs.setRadicado("");

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertNotNull(resultado.getRadicado());
        assertTrue(resultado.getRadicado().startsWith("R-"));
        assertFalse(resultado.getRadicado().isBlank());
    }

    @Test
    void testCreatePqrs_ConEstadoTextoPreexistente_DebeRespetarlo() {
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estadoPendiente);
        pqrs.setDescripcion("Test");
        pqrs.setEstadoTexto("ESTADO_CUSTOM");

        Pqrs resultado = pqrsService.createPqrs(pqrs);

        assertEquals("ESTADO_CUSTOM", resultado.getEstadoTexto());
    }
}
