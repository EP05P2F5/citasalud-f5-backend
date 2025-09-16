package com.feature5.pqrs.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")

public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrol")
    private Long idRol;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    public Rol() { }

    // Constructor
    public Rol(Long idRol) {
        this.idRol = idRol;
    }

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
