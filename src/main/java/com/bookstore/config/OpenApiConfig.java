package com.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Bookstore API")
                .version("1.0")
                .description("REST API for the Online Bookstore Application")
                .contact(new Contact()
                    .name("Bookstore Team")
                    .email("support@bookstore.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"))
                .addSchemas("ErrorResponse", new ObjectSchema()
                    .addProperty("timestamp", new DateTimeSchema())
                    .addProperty("status", new IntegerSchema())
                    .addProperty("error", new StringSchema())
                    .addProperty("message", new StringSchema())
                    .addProperty("path", new StringSchema()))
                .addSchemas("ValidationErrorResponse", new ObjectSchema()
                    .addProperty("timestamp", new DateTimeSchema())
                    .addProperty("status", new IntegerSchema())
                    .addProperty("error", new StringSchema())
                    .addProperty("message", new StringSchema())
                    .addProperty("path", new StringSchema())
                    .addProperty("errors", new ArraySchema()
                        .items(new ObjectSchema()
                            .addProperty("field", new StringSchema())
                            .addProperty("message", new StringSchema())))));
    }
} 