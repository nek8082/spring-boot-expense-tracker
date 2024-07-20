package com.nek.mysaasapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("keycloak")
@Data
public class KeycloakProperties {
    private String authServerUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private TechnicalUser technicalUser;

    @Data
    public static class TechnicalUser {
        private String username;
        private String password;
    }
}
