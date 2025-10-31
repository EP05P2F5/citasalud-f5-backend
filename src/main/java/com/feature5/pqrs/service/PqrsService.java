package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.mapper.PqrsMapper;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.PqrsRepository;
import com.feature5.pqrs.repository.TipoRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PqrsService {

    private static final Logger log = LoggerFactory.getLogger(PqrsService.class);
    private static final String ESTADO_RESPONDIDO = "RESPONDIDO";

    private final PqrsRepository pqrsRepository;
    private final EstadoRepository estadoRepository;
    private final TipoRepository tipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PqrsMapper pqrsMapper;

    public PqrsService(PqrsRepository pqrsRepository, 
                      EstadoRepository estadoRepository,
                      TipoRepository tipoRepository,
                      UsuarioRepository usuarioRepository,
                      PqrsMapper pqrsMapper) {
        this.pqrsRepository = pqrsRepository;
        this.estadoRepository = estadoRepository;
        this.tipoRepository = tipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pqrsMapper = pqrsMapper;
    }

    @Transactional(readOnly = true)
    public List<PqrsDTO> listarTodos() {
        return pqrsRepository.findAll()
                .stream()
                .map(pqrsMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PqrsDTO> obtenerPorId(Long id) {
        return pqrsRepository.findById(id)
                .map(pqrsMapper::toDTO);
    }

    @Transactional
    public PqrsDTO crearPqrs(Long usuarioId, Integer tipoId, Integer estadoId, PqrsDTO dto) {
        // Validar y obtener entidades relacionadas
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Tipo tipo = tipoRepository.findById(tipoId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo no encontrado"));
        
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        // Crear entidad Pqrs
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estado);
        pqrs.setEstadoTexto(dto.getEstado());
        pqrs.setDescripcion(dto.getDescripcion());
        pqrs.setFechaDeGeneracion(dto.getFechaDeGeneracion() != null ? dto.getFechaDeGeneracion().atStartOfDay() : LocalDateTime.now());
        pqrs.setRadicado(dto.getRadicado());
        pqrs.setFechaDeRespuesta(dto.getFechaDeRespuesta() != null ? dto.getFechaDeRespuesta().atStartOfDay() : null);
        pqrs.setRespuesta(dto.getRespuesta());

        Pqrs guardado = pqrsRepository.save(pqrs);
        log.info("PQRS creado con ID {}", guardado.getIdPqrs());
        return pqrsMapper.toDTO(guardado);
    }

    @Transactional
    public Optional<PqrsDTO> actualizarPqrs(Long id, Long usuarioId, Integer tipoId, Integer estadoId, PqrsDTO dto) {
        return pqrsRepository.findById(id).map(pqrsExistente -> {
            // Actualizar usuario si se proporciona
            if (usuarioId != null) {
                Usuario usuario = usuarioRepository.findById(usuarioId)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
                pqrsExistente.setUsuario(usuario);
            }

            // Actualizar tipo si se proporciona
            if (tipoId != null) {
                Tipo tipo = tipoRepository.findById(tipoId)
                        .orElseThrow(() -> new IllegalArgumentException("Tipo no encontrado"));
                pqrsExistente.setTipo(tipo);
            }

            // Actualizar estado si se proporciona
            if (estadoId != null) {
                Estado estado = estadoRepository.findById(estadoId)
                        .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
                pqrsExistente.setEstado(estado);
            }

            // Actualizar campos simples
            if (dto.getEstado() != null) pqrsExistente.setEstadoTexto(dto.getEstado());
            if (dto.getDescripcion() != null) pqrsExistente.setDescripcion(dto.getDescripcion());
            if (dto.getFechaDeGeneracion() != null) pqrsExistente.setFechaDeGeneracion(dto.getFechaDeGeneracion().atStartOfDay());
            if (dto.getRadicado() != null) pqrsExistente.setRadicado(dto.getRadicado());
            if (dto.getFechaDeRespuesta() != null) pqrsExistente.setFechaDeRespuesta(dto.getFechaDeRespuesta().atStartOfDay());
            if (dto.getRespuesta() != null) pqrsExistente.setRespuesta(dto.getRespuesta());

            Pqrs actualizado = pqrsRepository.save(pqrsExistente);
            log.info("PQRS con ID {} actualizado", id);
            return pqrsMapper.toDTO(actualizado);
        });
    }

    @Transactional
    public boolean eliminarPqrs(Long id) {
        if (pqrsRepository.existsById(id)) {
            pqrsRepository.deleteById(id);
            log.info("PQRS con ID {} eliminado", id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<PqrsDTO> buscarPorEstado(String estadoTexto) {
        return pqrsRepository.findByEstadoTexto(estadoTexto)
                .stream()
                .map(pqrsMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PqrsDTO> buscarPorUsuario(Long idUsuario) {
        return pqrsRepository.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(pqrsMapper::toDTO)
                .toList();
    }

    @Transactional
    public Optional<PqrsDTO> responderPqrs(Long id, String respuesta) {
        return pqrsRepository.findById(id).map(p -> {
            p.setRespuesta(respuesta);
            p.setFechaDeRespuesta(LocalDateTime.now());

            // Cambiar estado a RESPONDIDO si existe ese Estado; si no, solo texto
            Estado respondido = estadoRepository.findByDescripcion(ESTADO_RESPONDIDO).orElse(null);
            if (respondido != null) {
                p.setEstado(respondido);
                p.setEstadoTexto(ESTADO_RESPONDIDO);
            } else {
                p.setEstadoTexto(ESTADO_RESPONDIDO);
            }

            Pqrs actualizado = pqrsRepository.save(p);
            log.info("PQRS con ID {} respondido", id);
            return pqrsMapper.toDTO(actualizado);
        });
    }
}
