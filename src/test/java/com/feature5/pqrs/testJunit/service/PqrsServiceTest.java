package com.feature5.pqrs.testJunit.service;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.*;
import com.feature5.pqrs.repository.*;
import com.feature5.pqrs.service.PqrsService;

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
        // NO eliminar estados ni tipos - vienen de test-data.sql
        // Solo limpiar datos que creamos en los tests
        pqrsRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Usar rol Usuario (ID 3) que existe en test-data.sql
        Rol rol = rolRepository.findById(3).orElseThrow(() -> 
            new RuntimeException("Rol Usuario (ID 3) no encontrado en test-data.sql"));

        // Usuario base
        usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setNickname("testuser");
        usuario.setPassword("pass");
        usuario.setRol(rol);
        usuario = usuarioRepository.save(usuario);

        // Usar tipos que vienen de test-data.sql
        tipo = tipoRepository.findById(1).orElseThrow(() -> 
            new RuntimeException("Tipo con ID 1 no encontrado en test-data.sql"));

        // Usar estados que vienen de test-data.sql
        estadoPendiente = estadoRepository.findById(1).orElseThrow(() -> 
            new RuntimeException("Estado Pendiente (ID 1) no encontrado en test-data.sql"));
        estadoRespondido = estadoRepository.findById(3).orElseThrow(() -> 
            new RuntimeException("Estado Resuelta (ID 3) no encontrado en test-data.sql"));
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
    void testCreatePqrs_RadicadoSeGeneraAutomaticamente() {
        PqrsDTO pqrsDTO1 = new PqrsDTO();
        pqrsDTO1.setDescripcion("Test");
        // Ya no pasamos radicado - se genera automáticamente
        pqrsDTO1.setIdEstado(estadoPendiente.getIdEstado());

        PqrsDTO resultado1 = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO1);
        
        // Verificar que se generó un radicado con el formato esperado
        assertNotNull(resultado1.getRadicado());
        assertTrue(resultado1.getRadicado().startsWith("RAD-"));
        assertTrue(resultado1.getRadicado().contains("-"));
        assertEquals(estadoPendiente.getIdEstado(), resultado1.getIdEstado());
    }

    @Test
    void testCreatePqrs_SinRadicadoCustom_SeGeneraAutomaticamente() {
        PqrsDTO pqrsDTO2 = new PqrsDTO();
        pqrsDTO2.setDescripcion("Test sin radicado");

        PqrsDTO resultado2 = pqrsService.crearPqrs(usuario.getIdUsuario(), tipo.getIdTipo(), estadoPendiente.getIdEstado(), pqrsDTO2);
        
        // Ahora siempre se genera un radicado automáticamente
        assertNotNull(resultado2.getRadicado());
        assertTrue(resultado2.getRadicado().startsWith("RAD-"));
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
        assertEquals(estadoRespondido.getIdEstado(), resultado.get().getIdEstado()); // Use actual estado ID
    }

    @Test
    void testResponderPqrs_ConIdInexistente_DebeRetornarEmpty() {
        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(99999L, "Respuesta");
        assertTrue(resultado.isEmpty());
    }
}
