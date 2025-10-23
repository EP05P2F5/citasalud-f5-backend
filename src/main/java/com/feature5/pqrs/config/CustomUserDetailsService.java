package com.feature5.pqrs.config;

import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 🔹 Usuario de prueba temporal
        if (username.equals("admin")) {
            return new User("admin", "{noop}1234", Collections.emptyList());
        }

        // 🔹 Buscar usuario real en la base de datos
        Usuario usuario = usuarioRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 🔹 Asignar roles como authorities (vacío si no tiene rol)
        List<GrantedAuthority> authorities = Collections.emptyList();
        if (usuario.getRol() != null && usuario.getRol().getDescripcion() != null) {
            authorities = List.of(new SimpleGrantedAuthority(usuario.getRol().getDescripcion()));
        }

        // 🔹 Retornar objeto User de Spring Security
        return new User(usuario.getNickname(), usuario.getPassword(), authorities);
    }
}
