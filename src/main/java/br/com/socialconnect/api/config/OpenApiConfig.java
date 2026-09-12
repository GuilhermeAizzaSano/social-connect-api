package br.com.socialconnect.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SocialConnect API")
                        .description("API RESTful de gestão para instituições sociais (ITE005)")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("SocialConnect Team")));
    }
}
