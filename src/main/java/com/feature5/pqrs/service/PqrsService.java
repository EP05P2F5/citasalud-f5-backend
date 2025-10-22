package com.feature5.pqrs.service;

import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.PqrsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PqrsService {

    private final PqrsRepository pqrsRepository;
    private final EstadoRepository estadoRepository;

    public PqrsService(PqrsRepository pqrsRepository, EstadoRepository estadoRepository) {
        this.pqrsRepository = pqrsRepository;
        this.estadoRepository = estadoRepository;
    }

    public Pqrs createPqrs(Pqrs pqrs) {

        // DEBUG: Log para ver qué llega
        System.out.println("==== DEBUG createPqrs ====");
        System.out.println("idUsuario: " + pqrs.getIdUsuario());
        System.out.println("idTipo: " + pqrs.getIdTipo());
        System.out.println("descripcion: " + pqrs.getDescripcion());
        System.out.println("===========================");

        // Defaults de negocio
        if (pqrs.getFechaDeGeneracion() == null) {
            pqrs.setFechaDeGeneracion(LocalDate.now());
        }
        if (pqrs.getEstado() == null || pqrs.getEstado().isBlank()) {
            pqrs.setEstado("PENDIENTE");
        }

        // Resolver idEstado y normalizar descripcion a la forma canónica en BD
        if (pqrs.getIdEstado() == null) {
            var estadoOpt = estadoRepository.findByDescripcionIgnoreCase(pqrs.getEstado())
                    .or(() -> estadoRepository.findByDescripcion(pqrs.getEstado()));
            if (estadoOpt.isPresent()) {
                pqrs.setIdEstado(estadoOpt.get().getIdEstado());
                pqrs.setEstado(estadoOpt.get().getDescripcion());
            } else {
                pqrs.setIdEstado(1); // fallback
            }
        }

        if (pqrs.getRadicado() == null || pqrs.getRadicado().isBlank()) {
            pqrs.setRadicado(generateRadicado());
        }

        return pqrsRepository.save(pqrs);
    }

    private String generateRadicado() {
        return "R-" + System.currentTimeMillis();
    }
}