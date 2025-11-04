package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Pqrs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PqrsRepository extends JpaRepository<Pqrs, Long> {
    // Buscar por la ID del estado (a través de la FK)
    List<Pqrs> findByEstado_IdEstado(Integer idEstado);

    // Buscar por usuario (a través de la FK)
    List<Pqrs> findByUsuario_IdUsuario(Long idUsuario);
    
    // Buscar por tipo (a través de la FK)
    List<Pqrs> findByTipo_IdTipo(Integer idTipo);
}
