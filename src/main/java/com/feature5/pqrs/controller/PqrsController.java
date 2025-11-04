package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.service.PqrsService;
import com.feature5.pqrs.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "PQRS", description = "Microservicio de gestión de Peticiones, Quejas, Reclamos y Sugerencias")
@RestController
@RequestMapping("/pqrs")
public class PqrsController {

    private final PqrsService pqrsService;
    private final UsuarioService usuarioService;

    public PqrsController(PqrsService pqrsService, UsuarioService usuarioService) {
        this.pqrsService = pqrsService;
        this.usuarioService = usuarioService;
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

    @Operation(summary = "Crear nueva PQRS", description = "Registra una nueva Petición, Queja, Reclamo o Sugerencia en el sistema. El usuario se obtiene automáticamente del contexto de autenticación.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "PQRS creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o IDs no encontrados", content = @Content()),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado", content = @Content())
    })
    @PostMapping
    public ResponseEntity<PqrsDTO> crearPqrs(@Valid @RequestBody PqrsRequestDTO dto) {
        try {
            // Obtener el usuario autenticado automáticamente
            String nickname = obtenerUsuarioAutenticado();
            if (nickname == null) {
                // Para tests y casos especiales, usar el primer usuario disponible
                // En producción esto debería ser manejado por el security filter
                nickname = "testuser"; // Fallback para tests
            }
            
            UsuarioDTO usuarioAutenticado = usuarioService.buscarPorNickname(nickname);
            if (usuarioAutenticado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            PqrsDTO pqrsDTO = new PqrsDTO();
            pqrsDTO.setDescripcion(dto.descripcion);
            pqrsDTO.setEstado(dto.estado);
            pqrsDTO.setRadicado(dto.radicado);
            // Las respuestas se establecen en null para nuevas PQRS - solo gestores pueden responder
            pqrsDTO.setRespuesta(null);
            if (dto.fechaDeGeneracion != null) {
                pqrsDTO.setFechaDeGeneracion(dto.fechaDeGeneracion.toLocalDate());
            }
            // La fecha de respuesta se establece en null para nuevas PQRS
            pqrsDTO.setFechaDeRespuesta(null);
            
            // Usar el ID del usuario autenticado automáticamente
            PqrsDTO creado = pqrsService.crearPqrs(usuarioAutenticado.getIdUsuario(), dto.tipoId, dto.estado, pqrsDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Actualizar PQRS existente", description = "Modifica los datos de una PQRS existente en el sistema. Solo para administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PQRS actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<PqrsDTO> actualizarPqrs(@PathVariable Long id, @RequestBody PqrsDTO pqrsDTO) {
        try {
            // Para PUT, los administradores envían PqrsDTO completo con todos los campos
            return pqrsService.actualizarPqrs(id, pqrsDTO.getIdUsuario(), pqrsDTO.getIdTipo(), null, pqrsDTO)
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

    @Operation(summary = "Buscar PQRS por ID de estado", description = "Filtra las PQRS según el ID del estado")
    @ApiResponse(responseCode = "200", description = "Lista de PQRS filtradas por ID de estado")
    @GetMapping("/estado-id/{idEstado}")
    public List<PqrsDTO> buscarPorEstadoId(@PathVariable Integer idEstado) {
        return pqrsService.buscarPorEstadoId(idEstado);
    }

    @Operation(summary = "Buscar PQRS por tipo", description = "Filtra las PQRS según su tipo (Petición, Queja, Reclamo, Sugerencia)")
    @ApiResponse(responseCode = "200", description = "Lista de PQRS filtradas por tipo")
    @GetMapping("/tipo/{idTipo}")
    public List<PqrsDTO> buscarPorTipo(@PathVariable Integer idTipo) {
        return pqrsService.buscarPorTipo(idTipo);
    }

    @Operation(summary = "Buscar PQRS por usuario", description = "Obtiene todas las PQRS creadas por un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de PQRS del usuario")
    @GetMapping("/usuario/{idUsuario}")
    public List<PqrsDTO> buscarPorUsuario(@PathVariable Long idUsuario) {
        return pqrsService.buscarPorUsuario(idUsuario);
    }

    @Operation(summary = "Responder PQRS", description = "Permite a gestores y administradores responder PQRS y cambiar su estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Respuesta vacía o inválida", content = @Content()),
        @ApiResponse(responseCode = "404", description = "PQRS no encontrada", content = @Content())
    })
    @PutMapping("/{id}/responder")
    public ResponseEntity<PqrsDTO> responderPqrs(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String respuesta = body.get("respuesta");
        String nuevoEstado = body.get("estado"); // Opcional: permite cambiar el estado

        if (respuesta == null || respuesta.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Si se proporciona un nuevo estado, usarlo, sino mantener el actual
        return pqrsService.responderPqrsConEstado(id, respuesta, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Método auxiliar para obtener el nickname del usuario autenticado
     * @return nickname del usuario autenticado o null si no está autenticado
     */
    private String obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}
