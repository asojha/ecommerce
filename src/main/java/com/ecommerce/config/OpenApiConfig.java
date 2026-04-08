package com.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce Product Recommendation API")
                        .description("""
                                REST API for managing products and delivering personalised product recommendations.

                                **Scoring model** — every active product is scored against the submitted user profile.
                                Products that fail a hard-filter criterion (age range, gender, income, customer status,
                                loyalty tier) receive a score of 0 and are excluded from the response. Products that
                                pass all hard filters accumulate soft-boost points (category interest, tag overlap,
                                order count, account age) and are returned sorted by score descending.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ecommerce Team")
                                .email("team@ecommerce.example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development")
                ));
    }
}
