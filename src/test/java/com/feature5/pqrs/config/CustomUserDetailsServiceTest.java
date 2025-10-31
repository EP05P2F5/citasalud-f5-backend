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
        System.setProperty("ADMIN_USERNAME", "admin");
        System.setProperty("ADMIN_PASSWORD", "test_password");
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    void loadUserNotFoundThrows() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nope"));
    }

    @Test
    void loadUserWithRolePrefixCreatesAuthorities() {
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
    }

    @Test
    void loadUserWithoutRolePrefixAddsPrefix() {
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
        System.setProperty("ADMIN_USERNAME", "test_admin");
        System.setProperty("ADMIN_PASSWORD", "test_password");

        UserDetails details = service.loadUserByUsername("admin");

        assertEquals(System.getProperty("ADMIN_USERNAME"), details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
