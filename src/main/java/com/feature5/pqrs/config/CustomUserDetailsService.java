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
<<<<<<< HEAD
        // Aquí podrías consultar la BD (por ahora usuario simulado)
        if (username.equals("admin")) {
            return new User("admin", "{noop}1234", Collections.emptyList());
=======
        // Buscamos el usuario por nickname en la BD
        Usuario usuario = usuarioRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Mapear rol a authorities (si no hay rol, dejar lista vacía)
        List<GrantedAuthority> authorities = Collections.emptyList();
        if (usuario.getRol() != null && usuario.getRol().getDescripcion() != null) {
            authorities = List.of(new SimpleGrantedAuthority(usuario.getRol().getDescripcion()));
>>>>>>> origin/master
        }

        // Nota: password en la BD ya está encriptada por UsuarioService.registrarUsuario
        return new User(usuario.getNickname(), usuario.getPassword(), authorities);
    }
}

