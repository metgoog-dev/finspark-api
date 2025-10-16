package com.xyz.microfinance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microfinance API")
                        .description("REST API for microfinance operations")
                        .version("1.0.0")
                        .contact(new Contact().name("Microfinance Team")));
    }
}


