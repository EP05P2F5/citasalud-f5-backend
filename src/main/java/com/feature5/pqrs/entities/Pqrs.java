package com.feature5.pqrs.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "pqrs")

public class Pqrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPQRS")
    private Long idPqrs;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Column(name = "idtipo", nullable = false)
    private Integer idTipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_de_generacion")
    private LocalDate fechaDeGeneracion;

    @Column(name = "radicado")
    private String radicado;

    @Column(name = "estado")
    private String estado;

    // Algunas bases usan un FK obligatorio para estado (idestado). Si existe en la tabla, se mapea aquí.
    @Column(name = "idestado")
    private Integer idEstado;

    @Column(name = "fecha_de_respuesta")
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
}
