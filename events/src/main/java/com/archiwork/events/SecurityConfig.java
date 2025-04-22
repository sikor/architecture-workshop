package com.archiwork.events;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthFlowConfig oAuthFlowConfig;

    public SecurityConfig(OAuthFlowConfig oAuthFlowConfig){
        this.oAuthFlowConfig = oAuthFlowConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health")
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public OpenAPI openApiWithAzureAdSecurity() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("AzureAD",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl(oAuthFlowConfig.authorizationUrl())
                                                        .tokenUrl(oAuthFlowConfig.tokenUrl())
                                                        .scopes(new Scopes().addString(oAuthFlowConfig.scope(), "Access Events API"))
                                                )
                                        )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("AzureAD"));
    }
}