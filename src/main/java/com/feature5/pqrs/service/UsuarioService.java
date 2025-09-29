package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.mapper.UsuarioMapper;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    public UsuarioDTO login(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndPassword(email, password);
        return usuarioOpt.map(usuarioMapper::toDTO).orElse(null);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }

    public UsuarioDTO buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname)
                .map(usuarioMapper::toDTO)
                .orElse(null);
    }
}
