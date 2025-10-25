package com.feature5.pqrs.DTO;

import java.time.LocalDate;

public class PqrsDTO {
    private Long idPqrs;
    private Long idUsuario;
    private Integer idTipo;
    private String descripcion;
    private LocalDate fechaDeGeneracion;
    private String radicado;
    private String estado;
    private LocalDate fechaDeRespuesta;
    private String respuesta;

    // Constructor por defecto
    public PqrsDTO() {}

    // Constructor con parámetros principales
    public PqrsDTO(Long idUsuario, Integer idTipo, String descripcion) {
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.descripcion = descripcion;
        this.fechaDeGeneracion = LocalDate.now();
        this.estado = "PENDIENTE";
    }

    // Getters y setters
    public Long getIdPqrs() {
        return idPqrs;
    }

    public void setIdPqrs(Long idPqrs) {
        this.idPqrs = idPqrs;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaDeGeneracion() {
        return fechaDeGeneracion;
    }

    public void setFechaDeGeneracion(LocalDate fechaDeGeneracion) {
        this.fechaDeGeneracion = fechaDeGeneracion;
    }

    public String getRadicado() {
        return radicado;
    }

    public void setRadicado(String radicado) {
        this.radicado = radicado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaDeRespuesta() {
        return fechaDeRespuesta;
    }

    public void setFechaDeRespuesta(LocalDate fechaDeRespuesta) {
        this.fechaDeRespuesta = fechaDeRespuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
