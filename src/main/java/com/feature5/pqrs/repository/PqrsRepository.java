package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Pqrs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PqrsRepository extends JpaRepository<Pqrs, Long> {
    List<Pqrs> findByEstado(String estado);
    List<Pqrs> findByIdUsuario(Long idUsuario);
}