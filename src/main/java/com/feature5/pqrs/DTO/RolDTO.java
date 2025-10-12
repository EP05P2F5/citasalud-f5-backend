package com.feature5.pqrs.DTO;

public class RolDTO {
    private Long idRol;
    private String descripcion;

    // Constructor por defecto
    public RolDTO() {}

    // Constructor con parámetros
    public RolDTO(Long idRol, String descripcion) {
        this.idRol = idRol;
        this.descripcion = descripcion;
    }

    // Getters y setters
    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
