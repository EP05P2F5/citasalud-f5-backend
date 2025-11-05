package com.feature5.pqrs.DTO;

public class EstadoDTO {
    private Integer idEstado;
    private String descripcion;

    // Constructor por defecto
    public EstadoDTO() {}

    // Constructor con parámetros
    public EstadoDTO(Integer idEstado, String descripcion) {
        this.idEstado = idEstado;
        this.descripcion = descripcion;
    }

    // Getters y setters
    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}