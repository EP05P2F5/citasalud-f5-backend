package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    // Solo usar findById(Integer id) - ya incluido en JpaRepository
}
