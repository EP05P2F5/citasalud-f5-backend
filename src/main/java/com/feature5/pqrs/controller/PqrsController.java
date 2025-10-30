package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.PqrsRepository;
import com.feature5.pqrs.repository.TipoRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.service.PqrsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pqrs")
public class PqrsController {


    private final PqrsRepository pqrsRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final TipoRepository tipoRepository;
    private final PqrsService pqrsService;

    public PqrsController(PqrsRepository pqrsRepository,
                          UsuarioRepository usuarioRepository,
                          EstadoRepository estadoRepository,
                          TipoRepository tipoRepository,
                          PqrsService pqrsService) {
        this.pqrsRepository = pqrsRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.tipoRepository = tipoRepository;
        this.pqrsService = pqrsService;
    }

    // Listar todas las PQRS
    @GetMapping
    public ResponseEntity<List<Pqrs>> listarPqrs() {
        return ResponseEntity.ok(pqrsRepository.findAll());
    }

    // Obtener PQRS por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pqrs> obtenerPqrsPorId(@PathVariable Long id) {
        return pqrsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear PQRS (recibe DTO con IDs)
    @PostMapping
    @Transactional
    public ResponseEntity<Pqrs> crearPqrs(@Valid @RequestBody PqrsRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Tipo tipo = tipoRepository.findById(dto.tipoId).orElse(null);
        if (tipo == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Estado estado = estadoRepository.findById(dto.estadoId).orElse(null);
        if (estado == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estado);
        pqrs.setEstadoTexto(dto.estadoTexto);
        pqrs.setDescripcion(dto.descripcion);
        pqrs.setFechaDeGeneracion(dto.fechaDeGeneracion);
        pqrs.setRadicado(dto.radicado);
        pqrs.setFechaDeRespuesta(dto.fechaDeRespuesta);
        pqrs.setRespuesta(dto.respuesta);

        Pqrs saved = pqrsService.createPqrs(pqrs);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Actualizar PQRS (recibe DTO con IDs)
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Pqrs> actualizarPqrs(@PathVariable Long id, @RequestBody PqrsRequestDTO dto) {
        Optional<Pqrs> optional = pqrsRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pqrs existing = optional.get();

        // Validaciones y asignaciones simplificadas
        ResponseEntity<Pqrs> errorResponse;
        if ((errorResponse = validarYAsignarUsuario(dto, existing)) != null) return errorResponse;
        if ((errorResponse = validarYAsignarTipo(dto, existing)) != null) return errorResponse;
        if ((errorResponse = validarYAsignarEstado(dto, existing)) != null) return errorResponse;

        // Asignación directa de campos simples
        if (dto.estadoTexto != null) existing.setEstadoTexto(dto.estadoTexto);
        if (dto.descripcion != null) existing.setDescripcion(dto.descripcion);
        if (dto.fechaDeGeneracion != null) existing.setFechaDeGeneracion(dto.fechaDeGeneracion);
        if (dto.radicado != null) existing.setRadicado(dto.radicado);
        if (dto.fechaDeRespuesta != null) existing.setFechaDeRespuesta(dto.fechaDeRespuesta);
        if (dto.respuesta != null) existing.setRespuesta(dto.respuesta);

        Pqrs updated = pqrsRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    // Métodos privados auxiliares para reducir complejidad
    private ResponseEntity<Pqrs> validarYAsignarUsuario(PqrsRequestDTO dto, Pqrs existing) {
        if (dto.usuarioId == null) return null;
        Usuario u = usuarioRepository.findById(dto.usuarioId).orElse(null);
        if (u == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        existing.setUsuario(u);
        return null;
    }

    private ResponseEntity<Pqrs> validarYAsignarTipo(PqrsRequestDTO dto, Pqrs existing) {
        if (dto.tipoId == null) return null;
        Tipo t = tipoRepository.findById(dto.tipoId).orElse(null);
        if (t == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        existing.setTipo(t);
        return null;
    }

    private ResponseEntity<Pqrs> validarYAsignarEstado(PqrsRequestDTO dto, Pqrs existing) {
        if (dto.estadoId == null) return null;
        Estado e = estadoRepository.findById(dto.estadoId).orElse(null);
        if (e == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        existing.setEstado(e);
        return null;
    }

    // Eliminar PQRS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPqrs(@PathVariable Long id) {
        if (!pqrsRepository.existsById(id)) return ResponseEntity.notFound().build();
        pqrsRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Buscar PQRS por estadoTexto
    @GetMapping("/estado/{estadoTexto}")
    public ResponseEntity<List<Pqrs>> buscarPorEstado(@PathVariable String estadoTexto) {
        return ResponseEntity.ok(pqrsRepository.findByEstadoTexto(estadoTexto));
    }

    // Buscar PQRS por usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pqrs>> buscarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(pqrsRepository.findByUsuario_IdUsuario(idUsuario));
    }

    // Responder PQRS (solo respuesta y fecha)
    @RequestMapping(value = "/{id}/responder", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<Pqrs> responderPqrs(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String respuesta = body.get("respuesta");

        if (respuesta == null || respuesta.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Pqrs> pqrsActualizada = pqrsService.responderPqrs(id, respuesta);

        return pqrsActualizada
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
