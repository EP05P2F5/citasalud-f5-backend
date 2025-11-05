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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PqrsService {

    private static final Logger log = LoggerFactory.getLogger(PqrsService.class);
    private static final Integer ESTADO_RESPONDIDO_ID = 3; // ID para estado "Resuelta"

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
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + estadoId));

        // Crear entidad Pqrs
        Pqrs pqrs = new Pqrs();
        pqrs.setUsuario(usuario);
        pqrs.setTipo(tipo);
        pqrs.setEstado(estado);
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
            actualizarEntidadesAsociadas(pqrsExistente, usuarioId, tipoId, estadoId);
            actualizarCamposSimples(pqrsExistente, dto);

            Pqrs actualizado = pqrsRepository.save(pqrsExistente);
            log.info("PQRS con ID {} actualizado correctamente.", id);

            return pqrsMapper.toDTO(actualizado);
        });
    }


    private void actualizarEntidadesAsociadas(Pqrs pqrs, Long usuarioId, Integer tipoId, Integer estadoId) {
        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            pqrs.setUsuario(usuario);
        }

        if (tipoId != null) {
            Tipo tipo = tipoRepository.findById(tipoId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo no encontrado"));
            pqrs.setTipo(tipo);
        }

        if (estadoId != null) {
            Estado estado = estadoRepository.findById(estadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            pqrs.setEstado(estado);
        }
    }

    private void actualizarCamposSimples(Pqrs pqrs, PqrsDTO dto) {
        // El estado se actualiza a través de la FK en actualizarEntidadesAsociadas
        if (dto.getDescripcion() != null) {
            pqrs.setDescripcion(dto.getDescripcion());
        }
        if (dto.getFechaDeGeneracion() != null) {
            pqrs.setFechaDeGeneracion(dto.getFechaDeGeneracion().atStartOfDay());
        }
        if (dto.getRadicado() != null) {
            pqrs.setRadicado(dto.getRadicado());
        }
        if (dto.getFechaDeRespuesta() != null) {
            pqrs.setFechaDeRespuesta(dto.getFechaDeRespuesta().atStartOfDay());
        }
        if (dto.getRespuesta() != null) {
            pqrs.setRespuesta(dto.getRespuesta());
        }
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
    public List<PqrsDTO> buscarPorEstadoId(Integer idEstado) {
        return pqrsRepository.findByEstado_IdEstado(idEstado)
                .stream()
                .map(pqrsMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PqrsDTO> buscarPorTipo(Integer idTipo) {
        return pqrsRepository.findByTipo_IdTipo(idTipo)
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

            // Cambiar estado a RESPONDIDO usando ID
            Estado respondido = estadoRepository.findById(ESTADO_RESPONDIDO_ID).orElse(null);
            if (respondido != null) {
                p.setEstado(respondido);
            }

            Pqrs actualizado = pqrsRepository.save(p);
            log.info("PQRS con ID {} respondido con estado {}", id, p.getEstado().getDescripcion());
            return pqrsMapper.toDTO(actualizado);
        });
    }

    @Transactional
    public Optional<PqrsDTO> responderPqrsConEstado(Long id, String respuesta, Integer estadoId) {
        return pqrsRepository.findById(id).map(p -> {
            p.setRespuesta(respuesta);
            p.setFechaDeRespuesta(LocalDateTime.now());

            // Si se proporciona un estadoId, usarlo; sino usar RESPONDIDO por defecto
            Integer estadoFinal = (estadoId != null) ? estadoId : ESTADO_RESPONDIDO_ID;
            
            // Buscar el estado en la BD por ID
            Estado estado = estadoRepository.findById(estadoFinal).orElse(null);
            if (estado != null) {
                p.setEstado(estado);
            }

            Pqrs actualizado = pqrsRepository.save(p);
            log.info("PQRS con ID {} respondido con estado {}", id, estadoFinal);
            return pqrsMapper.toDTO(actualizado);
        });
    }
}
