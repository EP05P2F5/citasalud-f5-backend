package com.feature5.pqrs.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    CustomUserDetailsService service;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void loadUserNotFoundThrows() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nope"));
    }

    @Test
    void loadUserWithRoleCreatesAuthorities() {
        Rol rol = new Rol();
        rol.setDescripcion("ROLE_TEST");
        rol = rolRepository.save(rol);

        Usuario u = new Usuario();
        u.setNombre("Bob");
        u.setApellido("Test");
        u.setNickname("bob");
        u.setPassword("encoded");
        u.setEmail("bob@example.com");
        u.setRol(rol);
        usuarioRepository.save(u);

        UserDetails details = service.loadUserByUsername("bob");
        assertEquals("bob", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEST")));
    }

    @Test
    void loadAdminUserReturnsAdminAuthority() {
        // Test del usuario admin hardcodeado
        UserDetails details = service.loadUserByUsername("admin");
        assertEquals("admin", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertEquals(1, details.getAuthorities().size());
    }

    @Test
    void loadUserWithRolSinPrefixoROLE_DebeAgregarPrefijo() {
        Rol rol = new Rol();
        rol.setDescripcion("USER"); // Sin prefijo ROLE_
        rol = rolRepository.save(rol);

        Usuario u = new Usuario();
        u.setNombre("David");
        u.setApellido("Test");
        u.setNickname("david");
        u.setPassword("encoded");
        u.setEmail("david@example.com");
        u.setRol(rol);
        usuarioRepository.save(u);

        UserDetails details = service.loadUserByUsername("david");
        assertEquals("david", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
