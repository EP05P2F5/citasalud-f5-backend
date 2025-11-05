package com.feature5.pqrs.DTO;

import java.time.LocalDate;

public class PqrsDTO {
    private Long idPqrs;
    private Long idUsuario;
    private TipoDTO tipo;
    private String descripcion;
    private LocalDate fechaDeGeneracion;
    private String radicado;
    private EstadoDTO estado;
    private LocalDate fechaDeRespuesta;
    private String respuesta;

    // Keep these for backward compatibility and internal use
    private Integer idTipo;
    private Integer idEstado;

    // Constructor por defecto
    public PqrsDTO() {}

    // Constructor con parámetros principales
    public PqrsDTO(Long idUsuario, Integer idTipo, String descripcion) {
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.descripcion = descripcion;
        this.fechaDeGeneracion = LocalDate.now();
        this.idEstado = 1; // 1 = PENDIENTE
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

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
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

    // Getters y setters para los objetos completos
    public TipoDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoDTO tipo) {
        this.tipo = tipo;
    }

    public EstadoDTO getEstado() {
        return estado;
    }

    public void setEstado(EstadoDTO estado) {
        this.estado = estado;
    }
}
