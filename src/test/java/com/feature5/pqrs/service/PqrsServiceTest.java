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

        // Caso 2: Radicado vacío/null debe generarse, estadoTexto vacío/null debe tomarse del estado
        Pqrs pqrs2 = new Pqrs();
        pqrs2.setUsuario(usuario);
        pqrs2.setTipo(tipo);
        pqrs2.setEstado(estadoPendiente);
        pqrs2.setDescripcion("Test");
        pqrs2.setRadicado("");
        pqrs2.setEstadoTexto("   ");

        Pqrs resultado2 = pqrsService.createPqrs(pqrs2);
        assertNotNull(resultado2.getRadicado());
        assertTrue(resultado2.getRadicado().startsWith("R-"));
        assertEquals("PENDIENTE", resultado2.getEstadoTexto());
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
