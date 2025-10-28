package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsRequestDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Test
    void testCrearYObtenerPqrs() {
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "PENDIENTE";
        dto.descripcion = "Descripcion test";
        dto.fechaDeGeneracion = LocalDateTime.now();
        dto.radicado = "R-123";

        ResponseEntity<Pqrs> createdResp = pqrsController.crearPqrs(dto);
        assertEquals(201, createdResp.getStatusCodeValue());
        assertNotNull(createdResp.getBody());
        Long id = createdResp.getBody().getIdPqrs();

        ResponseEntity<Pqrs> fetched = pqrsController.obtenerPqrsPorId(id);
        assertEquals(200, fetched.getStatusCodeValue());
        assertEquals("Descripcion test", fetched.getBody().getDescripcion());
    }

    @Test
    void testActualizarYEliminarPqrs() {
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "PENDIENTE";
        dto.descripcion = "Original";
        dto.radicado = "R-50";
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();

        // Actualizar solo descripción (sin cambiar IDs)
        PqrsRequestDTO upd = new PqrsRequestDTO();
        upd.descripcion = "Modificado";
        upd.estadoTexto = "ACTUALIZADO";
        upd.radicado = "R-51";
        ResponseEntity<Pqrs> updated = pqrsController.actualizarPqrs(id, upd);
        assertEquals(200, updated.getStatusCodeValue());
        assertEquals("Modificado", updated.getBody().getDescripcion());
        assertEquals("ACTUALIZADO", updated.getBody().getEstadoTexto());
        assertEquals("R-51", updated.getBody().getRadicado());

        pqrsController.eliminarPqrs(id);
        assertFalse(pqrsRepository.existsById(id));
    }

    @Test
    void testListarPqrs() {
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "PENDIENTE";
        dto.descripcion = "PQRS 1";
        pqrsController.crearPqrs(dto);

        ResponseEntity<java.util.List<Pqrs>> response = pqrsController.listarPqrs();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().size() >= 1);
    }

    @Test
    void testBuscarPorEstadoYUsuario() {
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "ESPECIAL";
        dto.descripcion = "Test";
        pqrsController.crearPqrs(dto);

        ResponseEntity<java.util.List<Pqrs>> porEstado = pqrsController.buscarPorEstado("ESPECIAL");
        assertEquals(200, porEstado.getStatusCodeValue());
        assertTrue(porEstado.getBody().size() >= 1);

        ResponseEntity<java.util.List<Pqrs>> porUsuario = pqrsController.buscarPorUsuario(usuarioId);
        assertEquals(200, porUsuario.getStatusCodeValue());
        assertTrue(porUsuario.getBody().size() >= 1);
    }

    @Test
    void testResponderPqrs() {
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "PENDIENTE";
        dto.descripcion = "Para responder";
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();

        java.util.Map<String, String> respuesta = new java.util.HashMap<>();
        respuesta.put("respuesta", "Esta es la respuesta");

        ResponseEntity<Pqrs> response = pqrsController.responderPqrs(id, respuesta);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Esta es la respuesta", response.getBody().getRespuesta());
    }

    @Test
    void testErroresBasicos() {
        // Test 404
        assertEquals(404, pqrsController.obtenerPqrsPorId(99999L).getStatusCodeValue());
        assertEquals(404, pqrsController.eliminarPqrs(99999L).getStatusCodeValue());
        
        // Test 400 con respuesta vacía
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.descripcion = "Test";
        Long id = pqrsController.crearPqrs(dto).getBody().getIdPqrs();
        
        java.util.Map<String, String> respuestaVacia = new java.util.HashMap<>();
        respuestaVacia.put("respuesta", "   ");
        assertEquals(400, pqrsController.responderPqrs(id, respuestaVacia).getStatusCodeValue());

        // Test 400 al actualizar con IDs inválidos
        PqrsRequestDTO invalido = new PqrsRequestDTO();
        invalido.usuarioId = 99999L;
        assertEquals(400, pqrsController.actualizarPqrs(id, invalido).getStatusCodeValue());

        invalido = new PqrsRequestDTO();
        invalido.tipoId = 99999;
        assertEquals(400, pqrsController.actualizarPqrs(id, invalido).getStatusCodeValue());

        invalido = new PqrsRequestDTO();
        invalido.estadoId = 99999;
        assertEquals(400, pqrsController.actualizarPqrs(id, invalido).getStatusCodeValue());
    }
}

