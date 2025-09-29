package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setFechaDeNacimiento(usuario.getFechaDeNacimiento());
        dto.setDireccion(usuario.getDireccion());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setNickname(usuario.getNickname());
        dto.setPassword(usuario.getPassword());
        dto.setIdRol(usuario.getRol() != null ? usuario.getRol().getIdRol() : null);
        return dto;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setFechaDeNacimiento(dto.getFechaDeNacimiento());
        usuario.setDireccion(dto.getDireccion());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setNickname(dto.getNickname());
        usuario.setPassword(dto.getPassword());
        if (dto.getIdRol() != null) {
            usuario.setRol(new Rol(dto.getIdRol()));
        }
        return usuario;
    }

}


