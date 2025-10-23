package com.feature5.pqrs.config;

import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.repository.UsuarioRepository;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;

    public DataInitializer(UsuarioService usuarioService, UsuarioRepository usuarioRepository, EstadoRepository estadoRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public void run(String... args) {
        //  Verificar si el admin ya existe
        if (usuarioRepository.existsByEmail("admin@admin.com")) {
            log.info("El usuario administrador ya existe. No se volvera a crear.");
            return;
        }

    //  Crear el usuario administrador solo si no existe
        UsuarioDTO admin = new UsuarioDTO();
        admin.setNombre("Administrador");
        admin.setApellido("Principal");
        admin.setEmail("admin@admin.com");
        admin.setPassword("admin123");
        admin.setNickname("admin");
        admin.setDireccion("N/A");
        admin.setTelefono("0000000000");

        Rol rolAdmin = new Rol();
        rolAdmin.setDescripcion("ADMIN");
        admin.setRol(rolAdmin);

        usuarioService.registrarUsuario(admin);
        log.info(" Usuario administrador creado correctamente");

        // Asegurar estados básicos
        ensureEstadoExists("PENDIENTE");
        ensureEstadoExists("RESPONDIDO");
        ensureEstadoExists("CERRADO");
    }

    private void ensureEstadoExists(String descripcion) {
        estadoRepository.findByDescripcion(descripcion).orElseGet(() -> {
            Estado e = new Estado();
            e.setDescripcion(descripcion);
            Estado saved = estadoRepository.save(e);
            log.info("Estado '{}' asegurado con id {}", descripcion, saved.getIdEstado());
            return saved;
        });
    }
}


