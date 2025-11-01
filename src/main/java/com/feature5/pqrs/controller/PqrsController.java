package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.service.PqrsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "PQRS", description = "Microservicio de gestión de Peticiones, Quejas, Reclamos y Sugerencias")
@RestController
@RequestMapping("/pqrs")
public class PqrsController {

    private final PqrsService pqrsService;

    public PqrsController(PqrsService pqrsService) {
        this.pqrsService = pqrsService;
    }

    @Operation(summary = "Listar todas las PQRS", description = "Obtiene la lista completa de PQRS registradas en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de PQRS obtenida exitosamente")
    @GetMapping
    public List<PqrsDTO> listarPqrs() {
        return pqrsService.listarTodos();
    }

    @Operation(summary = "Obtener PQRS por ID", description = "Obtiene la información detallada de una PQRS específica mediante su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PQRS encontrada"),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<PqrsDTO> obtenerPqrsPorId(
            @Parameter(description = "ID de la PQRS a obtener")
            @PathVariable Long id) {
        return pqrsService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear nueva PQRS", description = "Registra una nueva Petición, Queja, Reclamo o Sugerencia en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "PQRS creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o IDs no encontrados", content = @Content())
    })
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

    @Operation(summary = "Actualizar PQRS existente", description = "Modifica los datos de una PQRS existente en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PQRS actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content())
    })
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
                    .map(actualizado -> ResponseEntity.status(HttpStatus.OK).body(actualizado))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Eliminar PQRS", description = "Elimina una PQRS del sistema de forma permanente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PQRS eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPqrs(@PathVariable Long id) {
        if (pqrsService.eliminarPqrs(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar PQRS por estado", description = "Filtra las PQRS según su estado (Pendiente, En Proceso, Resuelta, Cerrada)")
    @ApiResponse(responseCode = "200", description = "Lista de PQRS filtradas por estado")
    @GetMapping("/estado/{estadoTexto}")
    public List<PqrsDTO> buscarPorEstado(@PathVariable String estadoTexto) {
        return pqrsService.buscarPorEstado(estadoTexto);
    }

    @Operation(summary = "Buscar PQRS por usuario", description = "Obtiene todas las PQRS creadas por un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de PQRS del usuario")
    @GetMapping("/usuario/{idUsuario}")
    public List<PqrsDTO> buscarPorUsuario(@PathVariable Long idUsuario) {
        return pqrsService.buscarPorUsuario(idUsuario);
    }

    @Operation(summary = "Responder PQRS", description = "Registra la respuesta oficial a una PQRS y actualiza su fecha de respuesta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Respuesta vacía o inválida", content = @Content()),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada", content = @Content())
    })
    @PutMapping("/{id}/responder")
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
