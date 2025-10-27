package com.feature5.pqrs.config;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final RolRepository rolRepository;

    public DataInitializer(UsuarioService usuarioService,
                           UsuarioRepository usuarioRepository,
                           EstadoRepository estadoRepository,
                           RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {
        try {
            inicializarAdminSeguro();
            inicializarEstadosBasicos();
        } catch (Exception e) {
            log.warn(" Omitiendo inicialización de datos (ya existen o base protegida): {}", e.getMessage());
        }
    }

    private void inicializarAdminSeguro() {
        try {
            if (usuarioRepository.existsByEmail("admin@admin.com")) {
                log.info("Usuario administrador ya existe. No se crea nuevamente.");
                return;
            }

            Rol rolAdmin = rolRepository.findByDescripcion("ADMIN")
                    .orElseGet(() -> {
                        Rol r = new Rol();
                        r.setDescripcion("ADMIN");
                        return rolRepository.save(r);
                    });

            UsuarioDTO admin = new UsuarioDTO();
            admin.setNombre("Administrador");
            admin.setApellido("Principal");
            admin.setEmail("admin@admin.com");
            admin.setPassword("admin123");
            admin.setNickname("admin");
            admin.setDireccion("N/A");
            admin.setTelefono("0000000000");
            admin.setRol(rolAdmin);

            usuarioService.registrarUsuario(admin);
            log.info("Usuario administrador creado correctamente.");
        } catch (Exception e) {
            log.warn("No se pudo crear usuario administrador: {}", e.getMessage());
        }
    }

    private void inicializarEstadosBasicos() {
        ensureEstadoExists("PENDIENTE");
        ensureEstadoExists("RESPONDIDO");
        ensureEstadoExists("CERRADO");
    }

    private void ensureEstadoExists(String descripcion) {
        try {
            estadoRepository.findByDescripcion(descripcion).orElseGet(() -> {
                Estado e = new Estado();
                e.setDescripcion(descripcion);
                Estado saved = estadoRepository.save(e);
                log.info("Estado '{}' asegurado con id {}", descripcion, saved.getIdEstado());
                return saved;
            });
        } catch (Exception e) {
            log.warn("No se pudo asegurar el estado '{}': {}", descripcion, e.getMessage());
        }
    }
}
