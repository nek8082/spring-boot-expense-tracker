package com.nek.mysaasapp.config;

import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PRIVATE_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.VERIFY_EMAIL_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_SUCCESS_URL;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_PREMIUM;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_UNVERIFIED;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_VERIFIED;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import com.nek.mysaasapp.repository.AppUserRepository;
import com.nek.mysaasapp.services.SpringSecurityBasedUserService;

import com.nek.mysaasapp.services.interfaces.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @NonNull
    private final UserService springSecurityBasedUserService;
    @NonNull
    private final AppUserRepository userRepository;
    @NonNull
    private final CustomProperties customProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        springSecurityBasedUserService.setupUser();
        processUserRedirect(response);
    }

    /**
     * Redirects user to the appropriate page based on a user role, after a successful login.
     */
    private void processUserRedirect(HttpServletResponse response) throws IOException {
        Authentication loggedInAuth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = loggedInAuth.getAuthorities();

        if (customProperties.isRemovePayment()) {
            upgradeLoggedInUserToPremium();
            response.sendRedirect(CHECKOUT_SUCCESS_URL);
            return;
        }

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_PREMIUM))) {
            response.sendRedirect(CHECKOUT_SUCCESS_URL);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_VERIFIED))) {
            response.sendRedirect(PRIVATE_URL);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_UNVERIFIED))) {
            response.sendRedirect(VERIFY_EMAIL_URL);
        }
    }

    private void upgradeLoggedInUserToPremium() {
        springSecurityBasedUserService.getAuthenticatedUser().map(user -> {
            user.setUserRole(ROLE_PREMIUM);
            user.setPremiumValidTo(LocalDateTime.now().plusYears(500));
            userRepository.save(user);
            return user;
        });
    }
}
