package com.feature5.pqrs.service;

import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.PqrsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PqrsService {

    private final PqrsRepository pqrsRepository;

    public PqrsService(PqrsRepository pqrsRepository) {
        this.pqrsRepository = pqrsRepository;
    }

    public Pqrs createPqrs(Pqrs pqrs) {
        // Defaults de negocio
        if (pqrs.getFechaDeGeneracion() == null) {
            pqrs.setFechaDeGeneracion(LocalDate.now());
        }
        
        if (pqrs.getEstado() == null || pqrs.getEstado().isBlank()) {
            pqrs.setEstado("PENDIENTE");
        }

        if (pqrs.getRadicado() == null || pqrs.getRadicado().isBlank()) {
            pqrs.setRadicado(generateRadicado());
        }

        return pqrsRepository.save(pqrs);
    }

    public Optional<Pqrs> responderPqrs(Long id, String respuesta) {
        Optional<Pqrs> pqrsOptional = pqrsRepository.findById(id);
        
        if (pqrsOptional.isPresent()) {
            Pqrs pqrs = pqrsOptional.get();
            pqrs.setRespuesta(respuesta);
            pqrs.setFechaDeRespuesta(LocalDate.now());
            // Opcionalmente cambiar el estado a RESPONDIDA
            pqrs.setEstado("RESPONDIDA");
            return Optional.of(pqrsRepository.save(pqrs));
        }
        
        return Optional.empty();
    }

    private String generateRadicado() {
        return "R-" + System.currentTimeMillis();
    }
}