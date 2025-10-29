package com.feature5.pqrs.config;

import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // =========================================================
        //  ADMIN TEMPORAL PARA PRUEBAS LOCALES Y AUTOMATIZADAS
        // =========================================================
        // No contiene credenciales hardcodeadas.
        // Permite lectura tanto de variables de entorno como de system properties.
        if ("admin".equalsIgnoreCase(username)) {

            // Intenta obtener usuario y contraseña desde entorno o properties
            String adminUser = System.getenv("ADMIN_USERNAME");
            String adminPassword = System.getenv("ADMIN_PASSWORD");

            // Fallback para entornos de test (JUnit usa System properties)
            if (adminUser == null) {
                adminUser = System.getProperty("ADMIN_USERNAME");
            }
            if (adminPassword == null) {
                adminPassword = System.getProperty("ADMIN_PASSWORD");
            }

            // Si aún no existen, lanza excepción informativa
            if (adminUser == null || adminPassword == null) {
                throw new IllegalStateException("ADMIN_USERNAME or ADMIN_PASSWORD not configured in environment or test properties");
            }

            return User.builder()
                    .username(adminUser)
                    .password(passwordEncoder.encode(adminPassword))
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }

        // =========================================================
        //  USUARIO REAL DESDE BASE DE DATOS
        // =========================================================
        Usuario usuario = usuarioRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Determina el rol
        String rol = usuario.getRol() != null && usuario.getRol().getDescripcion() != null
                ? usuario.getRol().getDescripcion().toUpperCase()
                : "USER";

        if (!rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol;
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

        return new User(usuario.getNickname(), usuario.getPassword(), authorities);
    }
}
