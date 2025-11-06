package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Pqrs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PqrsRepository extends JpaRepository<Pqrs, Long> {
    // Buscar por la ID del estado (a través de la FK)
    List<Pqrs> findByEstado_IdEstado(Integer idEstado);

    // Buscar por usuario (a través de la FK)
    List<Pqrs> findByUsuario_IdUsuario(Long idUsuario);
    
    // Buscar por tipo (a través de la FK)
    List<Pqrs> findByTipo_IdTipo(Integer idTipo);
    
    // Buscar PQRS por prefijo de radicado (para generar consecutivos diarios automáticos)
    List<Pqrs> findByRadicadoStartingWithOrderByRadicadoDesc(String prefijoRadicado);
    
    // Buscar PQRS por radicado específico
    Optional<Pqrs> findByRadicado(String radicado);
}
