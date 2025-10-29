package com.feature5.pqrs.service;

import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.PqrsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PqrsService {

    private static final String ESTADO_RESPONDIDO = "RESPONDIDO";

    private final PqrsRepository pqrsRepository;
    private final EstadoRepository estadoRepository;

    public PqrsService(PqrsRepository pqrsRepository, EstadoRepository estadoRepository) {
        this.pqrsRepository = pqrsRepository;
        this.estadoRepository = estadoRepository;
    }

    @Transactional
    public Pqrs createPqrs(Pqrs pqrs) {
        return pqrsRepository.save(pqrs);
    }

    @Transactional
    public Optional<Pqrs> responderPqrs(Long id, String respuesta) {
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

            return pqrsRepository.save(p);
        });
    }
}
