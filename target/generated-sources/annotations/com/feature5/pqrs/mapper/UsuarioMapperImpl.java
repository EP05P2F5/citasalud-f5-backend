package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.DTO.UsuarioResponseDTO;
import com.feature5.pqrs.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-11T22:12:48-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDTO toDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();

        usuarioDTO.setApellido( usuario.getApellido() );
        usuarioDTO.setDireccion( usuario.getDireccion() );
        usuarioDTO.setEmail( usuario.getEmail() );
        usuarioDTO.setFechaDeNacimiento( usuario.getFechaDeNacimiento() );
        usuarioDTO.setIdUsuario( usuario.getIdUsuario() );
        usuarioDTO.setNickname( usuario.getNickname() );
        usuarioDTO.setNombre( usuario.getNombre() );
        usuarioDTO.setPassword( usuario.getPassword() );
        usuarioDTO.setRol( usuario.getRol() );
        usuarioDTO.setTelefono( usuario.getTelefono() );

        return usuarioDTO;
    }

    @Override
    public Usuario toEntity(UsuarioDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setApellido( dto.getApellido() );
        usuario.setDireccion( dto.getDireccion() );
        usuario.setEmail( dto.getEmail() );
        usuario.setFechaDeNacimiento( dto.getFechaDeNacimiento() );
        usuario.setIdUsuario( dto.getIdUsuario() );
        usuario.setNickname( dto.getNickname() );
        usuario.setNombre( dto.getNombre() );
        usuario.setPassword( dto.getPassword() );
        usuario.setRol( dto.getRol() );
        usuario.setTelefono( dto.getTelefono() );

        return usuario;
    }

    @Override
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();

        usuarioResponseDTO.setApellido( usuario.getApellido() );
        usuarioResponseDTO.setEmail( usuario.getEmail() );
        usuarioResponseDTO.setIdUsuario( usuario.getIdUsuario() );
        usuarioResponseDTO.setNickname( usuario.getNickname() );
        usuarioResponseDTO.setNombre( usuario.getNombre() );
        usuarioResponseDTO.setRol( usuario.getRol() );
        usuarioResponseDTO.setTelefono( usuario.getTelefono() );

        return usuarioResponseDTO;
    }
}
