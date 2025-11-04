package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.mapper.RolMapper;
import com.feature5.pqrs.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private static final Logger log = LoggerFactory.getLogger(RolService.class);

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolService(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    /**
     * Lista todos los roles.
     */
    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll()
                .stream()
                .map(rolMapper::toDto)
                .toList();
    }

    /**
     * Obtiene un rol por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<RolDTO> obtenerRolPorId(Integer id) {
        return rolRepository.findById(id)
                .map(rolMapper::toDto);
    }

    /**
     * Crea un nuevo rol.
     */
    public RolDTO crearRol(RolDTO rolDTO) {
        // Validar que no exista un rol con la misma descripción
        if (rolRepository.findByDescripcion(rolDTO.getDescripcion()).isPresent()) {
            log.warn("Intento de crear rol con descripción duplicada.");
            throw new IllegalArgumentException("Ya existe un rol con esa descripción.");
        }

        Rol rol = rolMapper.toEntity(rolDTO);
        Rol guardado = rolRepository.save(rol);
        log.info("Rol '{}' creado con ID {}", guardado.getDescripcion(), guardado.getIdRol());
        return rolMapper.toDto(guardado);
    }

    /**
     * Actualiza un rol existente.
     */
    public Optional<RolDTO> actualizarRol(Integer id, RolDTO rolDTO) {
        return rolRepository.findById(id)
                .map(rolExistente -> {
                    rolExistente.setDescripcion(rolDTO.getDescripcion());
                    Rol actualizado = rolRepository.save(rolExistente);
                    log.info("Rol con ID {} actualizado", id);
                    return rolMapper.toDto(actualizado);
                });
    }

    /**
     * Elimina un rol por su ID.
     */
    public boolean eliminarRol(Integer id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            log.info("Rol con ID {} eliminado", id);
            return true;
        }
        return false;
    }
}
