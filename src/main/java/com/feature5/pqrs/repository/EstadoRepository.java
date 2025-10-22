package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    Optional<Estado> findByDescripcion(String descripcion);
    Optional<Estado> findByDescripcionIgnoreCase(String descripcion);
}
