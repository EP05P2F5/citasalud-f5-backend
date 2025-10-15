package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.DTO.UsuarioResponseDTO;
import com.feature5.pqrs.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-15T13:07:40-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDTO toDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();

        usuarioDTO.setIdUsuario( usuario.getIdUsuario() );
        usuarioDTO.setNombre( usuario.getNombre() );
        usuarioDTO.setApellido( usuario.getApellido() );
        usuarioDTO.setFechaDeNacimiento( usuario.getFechaDeNacimiento() );
        usuarioDTO.setDireccion( usuario.getDireccion() );
        usuarioDTO.setEmail( usuario.getEmail() );
        usuarioDTO.setTelefono( usuario.getTelefono() );
        usuarioDTO.setNickname( usuario.getNickname() );
        usuarioDTO.setPassword( usuario.getPassword() );
        usuarioDTO.setRol( usuario.getRol() );

        return usuarioDTO;
    }

    @Override
    public Usuario toEntity(UsuarioDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setIdUsuario( dto.getIdUsuario() );
        usuario.setNombre( dto.getNombre() );
        usuario.setApellido( dto.getApellido() );
        usuario.setFechaDeNacimiento( dto.getFechaDeNacimiento() );
        usuario.setDireccion( dto.getDireccion() );
        usuario.setEmail( dto.getEmail() );
        usuario.setTelefono( dto.getTelefono() );
        usuario.setNickname( dto.getNickname() );
        usuario.setPassword( dto.getPassword() );
        usuario.setRol( dto.getRol() );

        return usuario;
    }

    @Override
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();

        usuarioResponseDTO.setIdUsuario( usuario.getIdUsuario() );
        usuarioResponseDTO.setNombre( usuario.getNombre() );
        usuarioResponseDTO.setApellido( usuario.getApellido() );
        usuarioResponseDTO.setEmail( usuario.getEmail() );
        usuarioResponseDTO.setTelefono( usuario.getTelefono() );
        usuarioResponseDTO.setNickname( usuario.getNickname() );
        usuarioResponseDTO.setRol( usuario.getRol() );

        return usuarioResponseDTO;
    }
}
