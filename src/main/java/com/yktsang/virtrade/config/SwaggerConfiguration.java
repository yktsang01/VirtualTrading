/*
 * SwaggerConfiguration.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Swagger configuration.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Configuration
public class SwaggerConfiguration {

    /**
     * Configures Swagger UI to prompt for the JWT.
     *
     * @return the Open API
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", this.createApiKeyScheme()))
                .info(new Info().title("Virtual Trading REST API")
                        .description("REST API for Virtual Trading web application.")
                        .version("1.0"));
    }

    /**
     * Creates the API key scheme.
     *
     * @return the security scheme
     */
    private SecurityScheme createApiKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("Bearer");
    }

}
