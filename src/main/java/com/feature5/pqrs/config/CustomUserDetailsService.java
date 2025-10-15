package com.feature5.pqrs.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 🔹 Aquí podrías consultar la BD (por ahora usuario simulado)
        if (username.equals("admin")) {
            return new User("admin", "{noop}1234", Collections.emptyList());
        }
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}

