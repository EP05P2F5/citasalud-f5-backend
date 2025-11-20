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
import java.util.Optional;

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

        // SEGURIDAD: Todos los usuarios registrados públicamente tienen rol ID 3 (usuario)
        // Solo los administradores pueden cambiar roles mediante PUT
        Rol rolUser = rolRepository.findById(3)
                .orElseThrow(() -> new IllegalStateException("Rol con ID 3 no existe en la base de datos"));
        usuario.setRol(rolUser);


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
                    // Seguridad: no loguear datos controlados por el usuario
                    log.info("Inicio de sesión exitoso (usuario autenticado).");
                    return usuarioMapper.toDto(usuario);
                })
                .orElseGet(() -> {
                    log.warn("Intento de inicio de sesión fallido (credenciales inválidas).");
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
     * Lista usuarios que pertenecen a un rol específico (por IdRol).
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosPorRol(Integer idRol) {
        List<UsuarioDTO> usuarios = usuarioRepository.findByRol_IdRol(idRol)
                .stream()
                .map(usuarioMapper::toDto)
                .toList();

        log.info("Se listaron {} usuarios con rol ID {}.", usuarios.size(), idRol);
        return usuarios;
    }

    /**
     * Lista los usuarios con rol Gestor (IdRol = 2).
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarGestores() {
        return listarUsuariosPorRol(2);
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

    /**
     * Busca un usuario por su ID.
     */
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDto)
                .orElse(null);
    }

    /**
     * Actualiza un usuario identificado por su ID.
     */
    @Transactional
    public Optional<UsuarioDTO> actualizarUsuarioPorId(Long id, UsuarioDTO dto) {
        return usuarioRepository.findById(id)
                .map(existing -> {
                    // Validar email nuevo
                    if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
                        if (usuarioRepository.existsByEmail(dto.getEmail())) {
                            throw new IllegalArgumentException("El correo ya está registrado.");
                        }
                        existing.setEmail(dto.getEmail());
                    }

                    // No permitir cambio de nickname vía este endpoint (identificador)
                    if (dto.getNickname() != null && !dto.getNickname().equals(existing.getNickname())) {
                        throw new IllegalArgumentException("No está permitido cambiar el nickname mediante este endpoint.");
                    }

                    if (dto.getNombre() != null) existing.setNombre(dto.getNombre());
                    if (dto.getApellido() != null) existing.setApellido(dto.getApellido());
                    if (dto.getFechaDeNacimiento() != null) existing.setFechaDeNacimiento(dto.getFechaDeNacimiento());
                    if (dto.getDireccion() != null) existing.setDireccion(dto.getDireccion());
                    if (dto.getTelefono() != null) existing.setTelefono(dto.getTelefono());

                    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }

                    if (dto.getRol() != null && dto.getRol().getIdRol() != null) {
                        Rol rol = rolRepository.findById(dto.getRol().getIdRol())
                                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
                        existing.setRol(rol);
                    }

                    Usuario guardado = usuarioRepository.save(existing);
                    log.info("Usuario con ID {} actualizado.", guardado.getIdUsuario());
                    return usuarioMapper.toDto(guardado);
                });
    }
    /**
     * Elimina un usuario por su ID. Retorna true si se eliminó, false si no existía.
     */
    @Transactional
    public boolean eliminarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioRepository.delete(usuario);
                    log.info("Usuario con ID {} eliminado.", id);
                    return true;
                }).orElse(false);
    }
}
