package com.feature5.pqrs.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "PQRS")

public class Pqrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPQRS")
    private Long idPqrs;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Column(name = "idTipo", nullable = false)
    private String idTipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fechaDeGeneracion")
    private LocalDate fechaDeGeneracion;

    @Column(name = "radicado")
    private String radicado;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fechaDeRespuesta")
    private LocalDate fechaDeRespuesta;

    @Column(name = "respuesta")
    private String respuesta;

    public Long getIdPqrs() {
        return idPqrs;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
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
