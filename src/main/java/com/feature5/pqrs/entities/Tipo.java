package com.feature5.pqrs.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipo")
    private Integer idTipo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

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
