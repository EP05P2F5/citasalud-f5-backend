package com.feature5.pqrs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "pqrs")
public class Pqrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpqrs")
    private Long idPqrs;

    // FK obligatoria -> usuario.idusuario
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    // FK obligatoria -> tipo.idtipo
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtipo", nullable = false)
    private Tipo tipo;

    // Texto libre (tu tabla lo tiene además de idestado)
    @Column(name = "descripcion")
    private String descripcion;

    // En DB es timestamp -> usar LocalDateTime
    @NotNull
    @Column(name = "fechadegeneracion")
    private LocalDateTime fechaDeGeneracion;

    @NotNull
    @Column(name = "radicado", unique = true)
    private String radicado;

    // FK obligatoria -> estado.idestado
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idestado", nullable = false)
    private Estado estado; // referencia a la entidad Estado

    @Column(name = "fechaderespuesta")
    private LocalDateTime fechaDeRespuesta;

    @Column(name = "respuesta")
    private String respuesta;

    // Getters/Setters
    public Long getIdPqrs() {
        return idPqrs;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Tipo getTipo() {
        return tipo;
    }
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaDeGeneracion() {
        return fechaDeGeneracion;
    }
    public void setFechaDeGeneracion(LocalDateTime fechaDeGeneracion) {
        this.fechaDeGeneracion = fechaDeGeneracion;
    }

    public String getRadicado() {
        return radicado;
    }
    public void setRadicado(String radicado) {
        this.radicado = radicado;
    }

    public Estado getEstado() {
        return estado;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaDeRespuesta() {
        return fechaDeRespuesta;
    }
    public void setFechaDeRespuesta(LocalDateTime fechaDeRespuesta) {
        this.fechaDeRespuesta = fechaDeRespuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
