package com.feature5.pqrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO vacío para respuestas de error sin contenido específico.
 * Evita que Swagger genere ejemplos automáticos para errores.
 */
@Schema(description = "Respuesta de error")
public class ErrorResponseDTO {
    // Clase intencionalmente vacía
}
