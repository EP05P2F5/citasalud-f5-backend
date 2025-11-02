package com.feature5.pqrs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS + CSRF off (JWT stateless)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // NOSONAR - CSRF no aplica con JWT stateless

                // Stateless (JWT)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Autorizaciones
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Errores (evita que 4xx/5xx se conviertan en 401)
                        .requestMatchers("/error", "/error/**").permitAll()

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Endpoints públicos (auth y pruebas)
                        .requestMatchers(
                                "/auth/**",
                                "/api/test/public",
                                "/api/test/env"
                        ).permitAll()

            // Permitir registro de usuario mediante POST /usuarios
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/usuarios").permitAll()


                        // Resto requiere autenticación
                        .anyRequest().authenticated()
                )

                // Manejo de excepciones
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // Filtro JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    // 401 si no hay token o es inválido
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "No autorizado: token requerido o inválido");
    }

    // 403 si hay token pero no permisos
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acceso prohibido: permisos insuficientes");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Para @Autowired AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // CORS básico para pruebas locales (ajusta origins según necesites)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Detectar entorno automáticamente
        String host = System.getenv("RENDER"); // Render define esta variable automáticamente
        String azureEnv = System.getenv("WEBSITE_SITE_NAME"); // Azure define esta
        boolean isRender = host != null;
        boolean isAzure = azureEnv != null;

        if (isRender) {
            cfg.setAllowedOriginPatterns(List.of("https://citasalud-feature5.onrender.com"));
        } else if (isAzure) {
            cfg.setAllowedOriginPatterns(List.of("https://citasalud-feature5.azurewebsites.net"));
        } else {
            // Entorno local
            cfg.setAllowedOriginPatterns(List.of(
                    "http://localhost:8080",
                    "http://127.0.0.1:8080",
                    "http://localhost:3000",
                    "http://127.0.0.1:5500"
            ));
        }

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
