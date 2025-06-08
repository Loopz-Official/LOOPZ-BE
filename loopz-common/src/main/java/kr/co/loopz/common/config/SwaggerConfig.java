package kr.co.loopz.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI createOpenAPI() {

        Info apiInfo = new Info()
                .title("Loopz Back-End API")
                .description("Loopz 백엔드 API 명세서")
                .version("1.0.0");
        String headerSchemeName = "Authorization";
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(headerSchemeName);

        Components components = new Components()
                .addSecuritySchemes(headerSchemeName,
                        new SecurityScheme()
                                .name(headerSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(apiInfo)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}