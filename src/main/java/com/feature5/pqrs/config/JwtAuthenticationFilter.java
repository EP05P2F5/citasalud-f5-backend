package com.feature5.pqrs.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    // Se marca como Lazy para evitar ciclo con CustomUserDetailsService
    private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Rutas públicas que no deben requerir token
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/",
            "/auth/login",
            "/usuarios/register",
            "/api/test/public"
    );

    public JwtAuthenticationFilter(JwtUtils jwtUtils, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Si la ruta es pública, saltamos la validación JWT
        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        logger.debug("Incoming request {} {} - Authorization header present: {}", request.getMethod(), path, authHeader != null);

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            if (authHeader != null && !authHeader.toLowerCase().startsWith("bearer ")) {
                logger.debug("Authorization header does not start with 'Bearer ' (value='{}')", authHeader);
            }
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).trim();
        try {
            username = jwtUtils.extractUsername(jwt);
        } catch (Exception e) {
            logger.info("Failed to extract username from token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("Extracted username from token: {}", username);

        var currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (username != null && (currentAuth == null || currentAuth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt) && username.equals(userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("JWT validated and authentication set for user={}", username);
            }
        }

        filterChain.doFilter(request, response);
    }

    // Método auxiliar: verifica si el path es público
    private boolean isPublic(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
}
