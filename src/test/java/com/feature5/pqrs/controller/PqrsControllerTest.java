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
        // Limpiar todo en orden correcto
        pqrsRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        tipoRepository.deleteAllInBatch();
        estadoRepository.deleteAllInBatch();
        rolRepository.deleteAllInBatch();

        //  Crear Rol (para cumplir FK)
        Rol rol = new Rol();
        rol.setDescripcion("TEST_ROLE");
        rol = rolRepository.save(rol);

        // Crear Usuario con nickname único
        Usuario u = new Usuario();
        u.setNombre("Test");
        u.setApellido("User");
        u.setNickname("testnick_" + UUID.randomUUID().toString().substring(0, 8));
        u.setPassword("pass");
        u.setRol(rol);
        usuarioRepository.save(u);
        usuarioId = u.getIdUsuario();

        // Crear Tipo
        Tipo t = new Tipo();
        t.setDescripcion("PETICION");
        tipoRepository.save(t);
        tipoId = t.getIdTipo();

        // Crear Estado
        Estado e = new Estado();
        e.setDescripcion("PENDIENTE");
        estadoRepository.save(e);
        estadoId = e.getIdEstado();
    }

    @Test
    void createAndGetFlow() {
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
        assertNotNull(fetched.getBody());
        assertEquals("Descripcion test", fetched.getBody().getDescripcion());
        assertEquals("PENDIENTE", fetched.getBody().getEstadoTexto());
        assertEquals("R-123", fetched.getBody().getRadicado());
    }

    @Test
    void putAndDeleteFlows() {
        // Crear base
        PqrsRequestDTO dto = new PqrsRequestDTO();
        dto.usuarioId = usuarioId;
        dto.tipoId = tipoId;
        dto.estadoId = estadoId;
        dto.estadoTexto = "PENDIENTE";
        dto.descripcion = "orig";
        dto.radicado = "R-50";
        ResponseEntity<Pqrs> created = pqrsController.crearPqrs(dto);
        Long id = created.getBody().getIdPqrs();

        // Actualizar (cambiamos solo descripción/estadoTexto)
        PqrsRequestDTO upd = new PqrsRequestDTO();
        upd.descripcion = "Modificado";
        upd.estadoTexto = "EN_PROCESO";
        ResponseEntity<Pqrs> updated = pqrsController.actualizarPqrs(id, upd);
        assertEquals(200, updated.getStatusCodeValue());
        assertEquals("Modificado", updated.getBody().getDescripcion());
        assertEquals("EN_PROCESO", updated.getBody().getEstadoTexto());

        // Eliminar
        ResponseEntity<?> deleted = pqrsController.eliminarPqrs(id);
        assertEquals(200, deleted.getStatusCodeValue());
        assertFalse(pqrsRepository.existsById(id));
    }
}
