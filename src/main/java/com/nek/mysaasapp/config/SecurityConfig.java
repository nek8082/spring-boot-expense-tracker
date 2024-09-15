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
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.VERIFY_EMAIL_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.WEBHOOK_URL;
import static com.nek.mysaasapp.rest.binding.PaymentVerificationControllerBinding.APPLE_PAYMENT_VERIFICATION_URL;
import static com.nek.mysaasapp.rest.binding.ProfileControllerBinding.PROFILE_URL;
import static com.nek.mysaasapp.rest.binding.StatsControllerBinding.STATS_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_DELETE_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_SAVE_URL;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_PREMIUM;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_UNVERIFIED;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_VERIFIED;
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
        http.cors(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .oauth2Login(oauth2Login -> oauth2Login.successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> logout.logoutSuccessUrl(PUBLIC_URL).permitAll())
                .oauth2Client(withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(WEBHOOK_URL));
    }

    /*
     * This is where we configure the security required for our endpoints and setup our app to serve as
     * an OAuth2 Resource Server, using JWT validation.
     */
    private void configureAuthorizations(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(ROOT_URL, PUBLIC_URL, "/webjars/**", "/css/**",
                        "/images/**", "/fragment/**", CONTACT_URL, PRIVACY_URL,
                        IMPRINT_URL, TERMS_OF_USE_URL, APPLE_PAYMENT_VERIFICATION_URL)
                .permitAll()
                .requestMatchers(POST, WEBHOOK_URL)
                .permitAll()
                .requestMatchers(VERIFY_EMAIL_URL)
                .hasAuthority(ROLE_UNVERIFIED)
                .requestMatchers(PRIVATE_URL, PROFILE_URL)
                .authenticated()
                .requestMatchers(PREMIUM_URL, TRANSACTION_SAVE_URL, STATS_URL,
                        EXPORT_URL, DATEV_EXPORT_URL, EXCEL_EXPORT_URL)
                .hasAuthority(ROLE_PREMIUM)
                .requestMatchers(POST, TRANSACTION_SAVE_URL,
                        TRANSACTION_DELETE_URL)
                .hasAuthority(ROLE_PREMIUM)
                .requestMatchers(CHECKOUT_URL)
                .hasAuthority(ROLE_VERIFIED)
                .anyRequest()
                .authenticated());
    }
}
