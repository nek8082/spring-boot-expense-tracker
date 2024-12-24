package com.nek.mysaasapp.config;

import static com.nek.mysaasapp.rest.binding.DashboardControllerBinding.PREMIUM_URL;
import static com.nek.mysaasapp.rest.binding.ExportControllerBinding.DATEV_EXPORT_URL;
import static com.nek.mysaasapp.rest.binding.ExportControllerBinding.EXCEL_EXPORT_URL;
import static com.nek.mysaasapp.rest.binding.ExportControllerBinding.EXPORT_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.CONTACT_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.IMPRINT_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.PRIVACY_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.TERMS_OF_USE_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PRIVATE_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.ROOT_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.WEBHOOK_URL;
import static com.nek.mysaasapp.rest.binding.PaymentVerificationControllerBinding.APPLE_PAYMENT_VERIFICATION_URL;
import static com.nek.mysaasapp.rest.binding.ProfileControllerBinding.PROFILE_URL;
import static com.nek.mysaasapp.rest.binding.StatsControllerBinding.STATS_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_DELETE_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_SAVE_URL;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_PREMIUM;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_NON_PREMIUM;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @NonNull
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureAuthorizations(http);
        configureWebSecurity(http);
        return http.build();
    }

    private void configureWebSecurity(HttpSecurity http) throws Exception {
        http
                .oauth2Login(oauth2Login -> oauth2Login.successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> logout.logoutSuccessUrl(PUBLIC_URL).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(WEBHOOK_URL));
    }

    /*
     * This is where we configure the security required for our endpoints.
     * The app is not configured as an OAuth2 Resource Server, as it does not validate incoming JWTs.
     * Authentication is handled via sessions established after a successful OAuth2 login.
     */
    private void configureAuthorizations(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(ROOT_URL, PUBLIC_URL, "/webjars/**", "/css/**",
                        "/images/**", "/fragment/**", CONTACT_URL, PRIVACY_URL,
                        IMPRINT_URL, TERMS_OF_USE_URL, APPLE_PAYMENT_VERIFICATION_URL)
                .permitAll()
                .requestMatchers(POST, WEBHOOK_URL)
                .permitAll()
                .requestMatchers(PRIVATE_URL, PROFILE_URL)
                .authenticated()
                .requestMatchers(PREMIUM_URL, TRANSACTION_SAVE_URL, STATS_URL,
                        EXPORT_URL, DATEV_EXPORT_URL, EXCEL_EXPORT_URL)
                .hasAuthority(ROLE_PREMIUM)
                .requestMatchers(POST, TRANSACTION_SAVE_URL,
                        TRANSACTION_DELETE_URL)
                .hasAuthority(ROLE_PREMIUM)
                .requestMatchers(CHECKOUT_URL)
                .hasAuthority(ROLE_NON_PREMIUM)
                .anyRequest()
                .authenticated());
    }
}
