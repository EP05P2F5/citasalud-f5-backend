package com.feature5.pqrs.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PqrsRequestDTO {
    // NO incluye usuarioId - se obtiene automáticamente del usuario autenticado
    
    @NotNull(message = "El tipo es obligatorio")
    public Integer tipoId;              // FK tipo
    
    @NotNull(message = "El estado es obligatorio")
    public Integer estadoId;            // FK estado
    
    @NotBlank(message = "La descripción es obligatoria")
    public String descripcion;
    
    public LocalDateTime fechaDeGeneracion;
    public String radicado;
    
    // REMOVIDO: fechaDeRespuesta y respuesta - solo los gestores pueden responder
}
