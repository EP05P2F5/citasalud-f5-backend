package com.feature5.pqrs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API PQRS - Feature5")
                        .version("1.0.0")
                        .description("""
### Descripción general
Esta API gestiona **Peticiones, Quejas, Reclamos y Sugerencias (PQRS)** dentro de una organización.  
Permite registrar solicitudes, asignar estados y generar respuestas de forma controlada.

---

### Autenticación
La API utiliza **JWT (JSON Web Token)** para autenticar solicitudes.  
Obtén tu token en `/auth/login` y luego pégalo en el botón **Authorize** (arriba a la derecha).



---

### Documentación interactiva
Usa esta interfaz para **probar los endpoints** directamente desde tu navegador.  
Asegúrate de autenticarte antes de acceder a los recursos protegidos.

---

*© 2025 Feature5 – Sistema PQRS | Universidad de Antioquia*
""")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Solo pega tu token JWT (sin 'Bearer ').")
                        )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Endpoints principales")
                .pathsToMatch("/**")
                .build();
    }

    /**
     * Configuración adicional del UI de Swagger.
     * Mejora la visualización de operaciones y modelos.
     */
    @Bean
    public OpenApiCustomizer swaggerUiCustomizer(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        swaggerUiConfigProperties.setDisplayOperationId(true);
        swaggerUiConfigProperties.setDisplayRequestDuration(true);
        swaggerUiConfigProperties.setDefaultModelsExpandDepth(1);
        swaggerUiConfigProperties.setDefaultModelExpandDepth(1);

        return openApi -> {

        };
    }
}
