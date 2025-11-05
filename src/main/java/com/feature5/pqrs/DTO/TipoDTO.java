package com.feature5.pqrs.DTO;

public class TipoDTO {
    private Integer idTipo;
    private String descripcion;

    // Constructor por defecto
    public TipoDTO() {}

    // Constructor con parámetros
    public TipoDTO(Integer idTipo, String descripcion) {
        this.idTipo = idTipo;
        this.descripcion = descripcion;
    }

    // Getters y setters
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
}