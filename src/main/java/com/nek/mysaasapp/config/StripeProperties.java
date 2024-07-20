package com.nek.mysaasapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("stripe")
@Data
public class StripeProperties {

    private String apiKey;
    private String price;
    private String webhookSecret;
    private String manageSubscriptionUrl;
    private String baseUrl;
}
