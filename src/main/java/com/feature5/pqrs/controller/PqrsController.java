package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.service.PqrsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
        summary = "Listar todas las PQRS",
        description = "Obtiene la lista completa de PQRS registradas en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de PQRS obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        )
    })
    @GetMapping
    public List<PqrsDTO> listarPqrs() {
        return pqrsService.listarTodos();
    }

    @Operation(
        summary = "Obtener PQRS por ID",
        description = "Obtiene la información detallada de una PQRS específica mediante su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "PQRS encontrada",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PqrsDTO> obtenerPqrsPorId(
            @Parameter(description = "ID de la PQRS a obtener")
            @PathVariable Long id) {
        return pqrsService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Crear nueva PQRS",
        description = "Registra una nueva Petición, Queja, Reclamo o Sugerencia en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "PQRS creada exitosamente",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o IDs no encontrados")
    })
    @PostMapping
    public ResponseEntity<PqrsDTO> crearPqrs(
            @Parameter(description = "Datos de la nueva PQRS (usuarioId, tipoId, estadoId, descripción)")
            @Valid @RequestBody PqrsRequestDTO dto) {
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

    @Operation(
        summary = "Actualizar PQRS existente",
        description = "Modifica los datos de una PQRS existente en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "PQRS actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PqrsDTO> actualizarPqrs(
            @Parameter(description = "ID de la PQRS a actualizar")
            @PathVariable Long id, 
            @Parameter(description = "Nuevos datos de la PQRS")
            @RequestBody PqrsRequestDTO dto) {
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

    @Operation(
        summary = "Eliminar PQRS",
        description = "Elimina una PQRS del sistema de forma permanente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PQRS eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPqrs(
            @Parameter(description = "ID de la PQRS a eliminar")
            @PathVariable Long id) {
        if (pqrsService.eliminarPqrs(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Buscar PQRS por estado",
        description = "Filtra las PQRS según su estado (Pendiente, En Proceso, Resuelta, Cerrada)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de PQRS filtradas por estado",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        )
    })
    @GetMapping("/estado/{estadoTexto}")
    public List<PqrsDTO> buscarPorEstado(
            @Parameter(description = "Texto del estado a filtrar (ej: Pendiente, Resuelta)")
            @PathVariable String estadoTexto) {
        return pqrsService.buscarPorEstado(estadoTexto);
    }

    @Operation(
        summary = "Buscar PQRS por usuario",
        description = "Obtiene todas las PQRS creadas por un usuario específico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de PQRS del usuario",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        )
    })
    @GetMapping("/usuario/{idUsuario}")
    public List<PqrsDTO> buscarPorUsuario(
            @Parameter(description = "ID del usuario creador de las PQRS")
            @PathVariable Long idUsuario) {
        return pqrsService.buscarPorUsuario(idUsuario);
    }

    @Operation(
        summary = "Responder PQRS",
        description = "Registra la respuesta oficial a una PQRS y actualiza su fecha de respuesta"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Respuesta registrada exitosamente",
            content = @Content(schema = @Schema(implementation = PqrsDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Respuesta vacía o inválida"),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada")
    })
    @PutMapping("/{id}/responder")
    public ResponseEntity<PqrsDTO> responderPqrs(
            @Parameter(description = "ID de la PQRS a responder")
            @PathVariable Long id, 
            @Parameter(description = "Objeto con campo 'respuesta' conteniendo el texto de respuesta")
            @RequestBody Map<String, String> body) {
        String respuesta = body.get("respuesta");

        if (respuesta == null || respuesta.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return pqrsService.responderPqrs(id, respuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
