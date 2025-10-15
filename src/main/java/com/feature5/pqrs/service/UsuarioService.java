package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.mapper.UsuarioMapper;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario, encriptando la contraseña y asignando correctamente el rol.
     */
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        // 🔍 Verificar si el correo o nickname ya existen
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            log.warn("Intento de registro con un email ya existente: {}", usuarioDTO.getEmail());
            throw new IllegalArgumentException("El correo ya está registrado: " + usuarioDTO.getEmail());
        }

        if (usuarioRepository.existsByNickname(usuarioDTO.getNickname())) {
            log.warn("Intento de registro con un nickname ya existente: {}", usuarioDTO.getNickname());
            throw new IllegalArgumentException("El nickname ya está registrado: " + usuarioDTO.getNickname());
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);

        //  Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

        //  Asignar o crear el rol correspondiente
        if (usuario.getRol() != null && usuario.getRol().getDescripcion() != null) {
            String descripcion = usuario.getRol().getDescripcion();

            Rol rolExistente = rolRepository.findByDescripcion(descripcion)
                    .orElseGet(() -> {
                        Rol nuevoRol = new Rol();
                        nuevoRol.setDescripcion(descripcion);
                        log.info("Rol '{}' no existía, se creó automáticamente.", descripcion);
                        return rolRepository.save(nuevoRol);
                    });

            usuario.setRol(rolExistente);
        }

        usuario = usuarioRepository.save(usuario);
        log.info(" Usuario '{}' creado correctamente con rol '{}'.",
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().getDescripcion() : "Sin rol");

        return usuarioMapper.toDTO(usuario);
    }

    /**
     * Valida las credenciales del usuario por nickname y contraseña.
     */
    public UsuarioDTO login(String nickname, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNickname(nickname);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Compara la contraseña ingresada con la encriptada en la BD
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                log.info("Inicio de sesión exitoso para el usuario '{}'.", nickname);
                return usuarioMapper.toDTO(usuario);
            } else {
                log.warn("Intento de inicio de sesión fallido: contraseña incorrecta para '{}'.", nickname);
            }
        } else {
            log.warn("Intento de inicio de sesión fallido: usuario '{}' no encontrado.", nickname);
        }

        return null; // Credenciales incorrectas
    }

    /**
     * Lista todos los usuarios registrados.
     */
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toDTO)
                .toList();
        log.info("Se listaron {} usuarios registrados.", usuarios.size());
        return usuarios;
    }

    /**
     * Busca un usuario por su nickname.
     */
    public UsuarioDTO buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname)
                .map(usuario -> {
                    log.info("Usuario '{}' encontrado correctamente.", nickname);
                    return usuarioMapper.toDTO(usuario);
                })
                .orElseGet(() -> {
                    log.warn("Usuario '{}' no encontrado.", nickname);
                    return null;
                });
    }
}

