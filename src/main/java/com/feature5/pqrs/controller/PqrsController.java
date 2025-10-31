package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.service.PqrsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pqrs")
public class PqrsController {

    private final PqrsService pqrsService;

    public PqrsController(PqrsService pqrsService) {
        this.pqrsService = pqrsService;
    }

    // Listar todas las PQRS
    @GetMapping
    public List<PqrsDTO> listarPqrs() {
        return pqrsService.listarTodos();
    }

    // Obtener PQRS por ID
    @GetMapping("/{id}")
    public ResponseEntity<PqrsDTO> obtenerPqrsPorId(@PathVariable Long id) {
        return pqrsService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear PQRS (recibe DTO con IDs)
    @PostMapping
    public ResponseEntity<PqrsDTO> crearPqrs(@Valid @RequestBody PqrsRequestDTO dto) {
        try {
            PqrsDTO pqrsDTO = new PqrsDTO();
            pqrsDTO.setDescripcion(dto.descripcion);
            pqrsDTO.setEstado(dto.estadoTexto);
            pqrsDTO.setRadicado(dto.radicado);
            pqrsDTO.setRespuesta(dto.respuesta);
            if (dto.fechaDeGeneracion != null) {
                pqrsDTO.setFechaDeGeneracion(dto.fechaDeGeneracion.toLocalDate());
            }
            if (dto.fechaDeRespuesta != null) {
                pqrsDTO.setFechaDeRespuesta(dto.fechaDeRespuesta.toLocalDate());
            }
            
            PqrsDTO creado = pqrsService.crearPqrs(dto.usuarioId, dto.tipoId, dto.estadoId, pqrsDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Actualizar PQRS (recibe DTO con IDs)
    @PutMapping("/{id}")
    public ResponseEntity<PqrsDTO> actualizarPqrs(@PathVariable Long id, @RequestBody PqrsRequestDTO dto) {
        try {
            PqrsDTO pqrsDTO = new PqrsDTO();
            pqrsDTO.setDescripcion(dto.descripcion);
            pqrsDTO.setEstado(dto.estadoTexto);
            pqrsDTO.setRadicado(dto.radicado);
            pqrsDTO.setRespuesta(dto.respuesta);
            if (dto.fechaDeGeneracion != null) {
                pqrsDTO.setFechaDeGeneracion(dto.fechaDeGeneracion.toLocalDate());
            }
            if (dto.fechaDeRespuesta != null) {
                pqrsDTO.setFechaDeRespuesta(dto.fechaDeRespuesta.toLocalDate());
            }

            return pqrsService.actualizarPqrs(id, dto.usuarioId, dto.tipoId, dto.estadoId, pqrsDTO)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Eliminar PQRS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPqrs(@PathVariable Long id) {
        if (pqrsService.eliminarPqrs(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Buscar PQRS por estadoTexto
    @GetMapping("/estado/{estadoTexto}")
    public List<PqrsDTO> buscarPorEstado(@PathVariable String estadoTexto) {
        return pqrsService.buscarPorEstado(estadoTexto);
    }

    // Buscar PQRS por usuario
    @GetMapping("/usuario/{idUsuario}")
    public List<PqrsDTO> buscarPorUsuario(@PathVariable Long idUsuario) {
        return pqrsService.buscarPorUsuario(idUsuario);
    }

    // Responder PQRS (solo respuesta y fecha)
    @RequestMapping(value = "/{id}/responder", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<PqrsDTO> responderPqrs(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String respuesta = body.get("respuesta");

        if (respuesta == null || respuesta.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return pqrsService.responderPqrs(id, respuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
