package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.mapper.UsuarioMapper;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en la base de datos, encriptando la contraseña
     * y asegurando que el rol exista o sea creado si no está registrado.
     */
    public UsuarioDTO registrarUsuario(UsuarioDTO dto) {
        // Validaciones de unicidad
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Intento de registro con email duplicado (oculto por seguridad).");
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        if (usuarioRepository.existsByNickname(dto.getNickname())) {
            log.warn("Intento de registro con nickname duplicado (oculto por seguridad).");
            throw new IllegalArgumentException("El nickname ya está registrado.");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Asociar rol (crear si no existe)
        if (dto.getRol() != null && dto.getRol().getDescripcion() != null) {
            String descripcion = dto.getRol().getDescripcion().trim();

            Rol rol = rolRepository.findByDescripcion(descripcion)
                    .orElseGet(() -> {
                        Rol nuevo = new Rol();
                        nuevo.setDescripcion(descripcion);

                        // Seguridad: evitar exponer datos controlados por el usuario
                        log.info("Se creó automáticamente un nuevo rol en el sistema (valor oculto por seguridad).");

                        return rolRepository.save(nuevo);
                    });

            usuario.setRol(rol);
        } else {
            log.warn("El usuario no tenía rol asignado; se aplicará 'USER' por defecto.");
            Rol rolDefault = rolRepository.findByDescripcion("USER")
                    .orElseGet(() -> {
                        Rol nuevo = new Rol();
                        nuevo.setDescripcion("USER");
                        return rolRepository.save(nuevo);
                    });
            usuario.setRol(rolDefault);
        }


        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario '{}' registrado con rol '{}'.", guardado.getNickname(), guardado.getRol().getDescripcion());

        return usuarioMapper.toDto(guardado);
    }

    /**
     * Valida credenciales de acceso.
     */
    @Transactional(readOnly = true)
    public UsuarioDTO login(String nickname, String password) {
        return usuarioRepository.findByNickname(nickname)
                .filter(usuario -> passwordEncoder.matches(password, usuario.getPassword()))
                .map(usuario -> {
                    log.info("Inicio de sesión exitoso para '{}'.", nickname);
                    return usuarioMapper.toDto(usuario);
                })
                .orElseGet(() -> {
                    log.warn("Intento de login fallido para '{}'.", nickname);
                    return null;
                });
    }

    /**
     * Lista todos los usuarios registrados.
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toDto)
                .toList();

        log.info("Se listaron {} usuarios registrados.", usuarios.size());
        return usuarios;
    }

    /**
     * Busca un usuario por su nickname.
     */
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname)
                .map(usuarioMapper::toDto)
                .orElse(null);
    }
}
