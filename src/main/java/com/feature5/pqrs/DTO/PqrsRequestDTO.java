package com.feature5.pqrs.DTO;

import java.time.LocalDateTime;

public class PqrsRequestDTO {
    public Long usuarioId;              // FK usuario
    public Integer tipoId;              // FK tipo
    public Integer estadoId;            // FK estado (obligatoria)
    public String estadoTexto;          // Texto opcional (coexiste con idestado)
    public String descripcion;
    public LocalDateTime fechaDeGeneracion;
    public String radicado;
    public LocalDateTime fechaDeRespuesta;
    public String respuesta;
}
