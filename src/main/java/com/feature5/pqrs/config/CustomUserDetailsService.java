package com.feature5.pqrs.config;

import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${admin.username}")
    private String adminUsername;
    
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // =========================================================
        //  ADMIN TEMPORAL PARA PRUEBAS LOCALES Y AUTOMATIZADAS
        // =========================================================
        if ("admin".equalsIgnoreCase(username)) {
            return User.builder()
                    .username(adminUsername)
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
