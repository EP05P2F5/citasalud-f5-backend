package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Pqrs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PqrsRepository extends JpaRepository<Pqrs, Long> {
    // Antes: findByEstado(String) -> ahora el campo texto se llama estadoTexto
    List<Pqrs> findByEstadoTexto(String estadoTexto);

    // Antes: findByIdUsuario(Long) -> ahora es relación: usuario.idUsuario
    List<Pqrs> findByUsuario_IdUsuario(Long idUsuario);
}
