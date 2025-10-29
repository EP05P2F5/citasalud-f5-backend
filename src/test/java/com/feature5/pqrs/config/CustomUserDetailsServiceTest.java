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
        Rol rolConPrefijo = new Rol();
        rolConPrefijo.setDescripcion("ROLE_TEST");
        rolConPrefijo = rolRepository.save(rolConPrefijo);

        Usuario u1 = new Usuario();
        u1.setNombre("Bob");
        u1.setApellido("Test");
        u1.setNickname("bob");
        u1.setPassword("encoded");
        u1.setEmail("bob@example.com");
        u1.setRol(rolConPrefijo);
        usuarioRepository.save(u1);

        UserDetails details = service.loadUserByUsername("bob");
        assertEquals("bob", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEST")));

        // También probar que roles sin prefijo ROLE_ lo agregan automáticamente
        Rol rolSinPrefijo = new Rol();
        rolSinPrefijo.setDescripcion("USER");
        rolSinPrefijo = rolRepository.save(rolSinPrefijo);

        Usuario u2 = new Usuario();
        u2.setNombre("David");
        u2.setApellido("Test");
        u2.setNickname("david");
        u2.setPassword("encoded");
        u2.setEmail("david@example.com");
        u2.setRol(rolSinPrefijo);
        usuarioRepository.save(u2);

        UserDetails details2 = service.loadUserByUsername("david");
        assertEquals("david", details2.getUsername());
        assertTrue(details2.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadAdminUserReturnsAdminAuthority() {
        // Test del usuario admin hardcodeado
        UserDetails details = service.loadUserByUsername("admin");
        assertEquals("admin", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertEquals(1, details.getAuthorities().size());
    }
}
