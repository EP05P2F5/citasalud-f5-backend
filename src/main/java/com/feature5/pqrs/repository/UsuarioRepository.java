package com.feature5.pqrs.repository;

import com.feature5.pqrs.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNickname(String nickname);

    // Para login (buscar por email y password, aunque normalmente se encripta)
    Optional<Usuario> findByEmailAndPassword(String email, String password);
}

