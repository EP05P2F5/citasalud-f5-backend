package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.entities.Estado;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PqrsControllerTest {

    @Autowired private PqrsController pqrsController;
    @Autowired private PqrsRepository pqrsRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TipoRepository tipoRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private RolRepository rolRepository;

    private Long usuarioId;
    private Integer tipoId;
    private Integer estadoId;

    @BeforeEach
    void setup() {
        pqrsRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        tipoRepository.deleteAllInBatch();
        estadoRepository.deleteAllInBatch();
        rolRepository.deleteAllInBatch();

        Rol rol = new Rol();
        rol.setDescripcion("TEST_ROLE");
        rol = rolRepository.save(rol);

        Usuario u = new Usuario();
        u.setNombre("Test");
        u.setApellido("User");
        u.setNickname("testnick_" + UUID.randomUUID().toString().substring(0, 8));
        u.setPassword("pass");
        u.setRol(rol);
        usuarioRepository.save(u);
        usuarioId = u.getIdUsuario();

        Tipo t = new Tipo();
        t.setDescripcion("PETICION");
        tipoRepository.save(t);
        tipoId = t.getIdTipo();

        Estado e = new Estado();
        e.setDescripcion("PENDIENTE");
        estadoRepository.save(e);
        estadoId = e.getIdEstado();
    }

    private PqrsRequestDTO createBasicDTO() {
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "PENDIENTE";
        dto.descripcion = "Descripcion test";
        dto.radicado = "R-" + UUID.randomUUID().toString().substring(0, 8);
        return dto;
    }

    @Test
    void testCrearYObtenerPqrs() {
        PqrsRequestDTO dto = createBasicDTO();
        dto.fechaDeGeneracion = LocalDateTime.now();

        ResponseEntity<PqrsDTO> createdResp = pqrsController.crearPqrs(dto);
        assertEquals(201, createdResp.getStatusCode().value());
        assertNotNull(createdResp.getBody());
        Long id = createdResp.getBody().getIdPqrs();

        ResponseEntity<PqrsDTO> fetched = pqrsController.obtenerPqrsPorId(id);
        assertEquals(200, fetched.getStatusCode().value());
        assertEquals("Descripcion test", fetched.getBody().getDescripcion());
    }

    @Test
    void testActualizarYEliminarPqrs() {
        PqrsRequestDTO dto = createBasicDTO();
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();

        PqrsRequestDTO upd = new PqrsRequestDTO();
        upd.descripcion = "Modificado";
        upd.estadoTexto = "ACTUALIZADO";
        upd.radicado = "R-51";
        ResponseEntity<PqrsDTO> updated = pqrsController.actualizarPqrs(id, upd);
        assertEquals(200, updated.getStatusCode().value());
        assertEquals("Modificado", updated.getBody().getDescripcion());

        pqrsController.eliminarPqrs(id);
        assertFalse(pqrsRepository.existsById(id));
    }

    @Test
    void testListarPqrs() {
        PqrsRequestDTO dto = createBasicDTO();
        pqrsController.crearPqrs(dto);

        List<PqrsDTO> response = pqrsController.listarPqrs();
        assertTrue(response.size() >= 1);
    }

    @Test
    void testBuscarPorEstadoYUsuario() {
        PqrsRequestDTO dto = createBasicDTO();
        dto.estadoTexto = "ESPECIAL";
        pqrsController.crearPqrs(dto);

        List<PqrsDTO> porEstado = pqrsController.buscarPorEstado("ESPECIAL");
        assertTrue(porEstado.size() >= 1);

        List<PqrsDTO> porUsuario = pqrsController.buscarPorUsuario(usuarioId);
        assertTrue(porUsuario.size() >= 1);
    }

    @Test
    void testResponderPqrs() {
        PqrsRequestDTO dto = createBasicDTO();
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();

        java.util.Map<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("respuesta", "Esta es la respuesta");

        ResponseEntity<PqrsDTO> response = pqrsController.responderPqrs(id, respuesta);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Esta es la respuesta", response.getBody().getRespuesta());
    }

    @Test
    void testErroresBasicos() {
        assertEquals(404, pqrsController.obtenerPqrsPorId(99999L).getStatusCode().value());
        assertEquals(404, pqrsController.eliminarPqrs(99999L).getStatusCode().value());
        assertEquals(404, pqrsController.actualizarPqrs(99999L, new PqrsRequestDTO()).getStatusCode().value());
    }

    @Test
    void testCrearPqrsConIdsInvalidos() {
        PqrsRequestDTO dto = createBasicDTO();
        dto.usuarioId = 99999L;
        assertEquals(400, pqrsController.crearPqrs(dto).getStatusCode().value());

        dto = createBasicDTO();
        dto.tipoId = 99999;
        assertEquals(400, pqrsController.crearPqrs(dto).getStatusCode().value());

        dto = createBasicDTO();
        dto.estadoId = 99999;
        assertEquals(400, pqrsController.crearPqrs(dto).getStatusCode().value());
    }

    @Test
    void testActualizarPqrsConIdsInvalidos() {
        PqrsRequestDTO dto = createBasicDTO();
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();

        PqrsRequestDTO invalido = new PqrsRequestDTO();
        invalido.usuarioId = 99999L;
        assertEquals(400, pqrsController.actualizarPqrs(id, invalido).getStatusCode().value());

        invalido = new PqrsRequestDTO();
        invalido.tipoId = 99999;
        assertEquals(400, pqrsController.actualizarPqrs(id, invalido).getStatusCode().value());

        invalido = new PqrsRequestDTO();
        invalido.estadoId = 99999;
        assertEquals(400, pqrsController.actualizarPqrs(id, invalido).getStatusCode().value());
    }

    @Test
    void testResponderPqrsConRespuestaInvalida() {
        PqrsRequestDTO dto = createBasicDTO();
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();

        java.util.Map<String, String> respuestaVacia = new java.util.HashMap<>();
        respuestaVacia.put("respuesta", "   ");
        assertEquals(400, pqrsController.responderPqrs(id, respuestaVacia).getStatusCode().value());

        java.util.Map<String, String> respuestaNull = new java.util.HashMap<>();
        respuestaNull.put("respuesta", null);
        assertEquals(400, pqrsController.responderPqrs(id, respuestaNull).getStatusCode().value());

        java.util.Map<String, String> respuestaValida = new java.util.HashMap<>();
        respuestaValida.put("respuesta", "Respuesta valida");
        assertEquals(404, pqrsController.responderPqrs(99999L, respuestaValida).getStatusCode().value());
    }
}
