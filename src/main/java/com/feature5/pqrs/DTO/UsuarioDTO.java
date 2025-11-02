package com.feature5.pqrs.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private Long idUsuario;

    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "apellido es obligatorio")
    private String apellido;

    private LocalDate fechaDeNacimiento;
    private String direccion;

    @NotBlank(message = "email es obligatorio")
    @Email(message = "email no es válido")
    private String email;

    private String telefono;

    @NotBlank(message = "nickname es obligatorio")
    private String nickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "rol es obligatorio")
    private RolDTO rol;

    // Getters y setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }
    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) { this.fechaDeNacimiento = fechaDeNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RolDTO getRol() { return rol; }
    public void setRol(RolDTO rol) { this.rol = rol; }
}
