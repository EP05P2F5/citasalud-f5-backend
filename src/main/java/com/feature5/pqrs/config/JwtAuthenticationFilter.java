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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Endpoints públicos (sin autenticación) - rutas sin método específico
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/auth/login",
        "/api/test/public",
        "/api/test/env",
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui.html"
    );

    // Precalcular lista normalizada una vez
    private static final Set<String> PUBLIC_ENDPOINTS_NORMALIZED = PUBLIC_ENDPOINTS.stream()
            .map(JwtAuthenticationFilter::normalizePath)
            .collect(Collectors.toUnmodifiableSet());

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
        String method = request.getMethod();

        // Permitir POST /usuarios como endpoint público de registro
        String normalizedForCheck = normalizePath(path);
        if ("/usuarios".equals(normalizedForCheck) && "POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Permitir rutas públicas sin autenticación (otras rutas)
        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        log.debug("Incoming request {} {} - Authorization header present: {}", request.getMethod(), path, authHeader != null);

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            if (authHeader != null && !authHeader.toLowerCase().startsWith("bearer ")) {
                log.debug("Authorization header does not start with 'Bearer ' (value='{}')", authHeader);
            }
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).trim();
        try {
            username = jwtUtils.extractUsername(jwt);
        } catch (Exception e) {
            log.info("Failed to extract username from token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Extracted username from token: {}", username);

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
                log.debug("JWT validated and authentication set for user={}", username);
            }
        }
        filterChain.doFilter(request, response);
    }

    // --- Métodos auxiliares seguros y sin regex ---

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "";
        String s = path.toLowerCase(Locale.ROOT);
        int end = s.length();
        while (end > 1 && s.charAt(end - 1) == '/') {
            end--;
        }
        return s.substring(0, end);
    }

    private boolean isPublic(String path) {
        String normalized = normalizePath(path);
        // Puedes cambiar equals() por startsWith() si quieres permitir prefijos (ej: /auth/*)
        return PUBLIC_ENDPOINTS_NORMALIZED.contains(normalized);
    }
}
