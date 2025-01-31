package com.nek.mysaasapp.services;

import static java.util.Collections.singletonList;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.repository.AppUserRepository;
import com.nek.mysaasapp.services.interfaces.CancelSubscriptionService;
import com.nek.mysaasapp.services.interfaces.IdpService;
import com.nek.mysaasapp.services.interfaces.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpringSecurityBasedUserService implements UserService {

    @NonNull
    private final AppUserRepository appUserRepository;
    @NonNull
    private final IdpService keycloakService;
    @NonNull
    private final CancelSubscriptionService cancelSubscriptionService;

    @Override
    public void setupUser() {
        saveUserOnFirstLogin();
        setUserRoleAfterAuth();
    }

    @Override
    public void deleteUser() {
        getLoggedInOidcUser().ifPresent(keycloakService::deleteUserFromIdp);
        getAuthenticatedUser().ifPresent(appUser -> {
            if (ROLE_PREMIUM.equals(appUser.getUserRole())) {
                cancelSubscriptionService.cancelSubscription(appUser);
            }
            appUserRepository.delete(appUser);
        });
    }

    @Override
    public Optional<AppUser> getAuthenticatedUser() {
        Authentication authentication = getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof DefaultOidcUser oidcUser)) {
            return Optional.empty();
        }
        return appUserRepository.findByEmail(oidcUser.getEmail());
    }

    @Transactional
    private void saveUserOnFirstLogin() {
        Optional<OidcUser> loggedInOidcUser = getLoggedInOidcUser();
        if (loggedInOidcUser.isEmpty()) {
            log.warn("No authenticated oidc user present, aborting");
            return;
        }
        String email = loggedInOidcUser.get().getEmail();
        log.info("Checking if user already exists");
        Optional<AppUser> existingUser = appUserRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            log.info("This is not the first login, skipping procedure");
            return;
        }
        createUser(email);
    }

    private void createUser(String email) {
        AppUser newUser = new AppUser();
        newUser.setEmail(email);
        newUser.setPremiumValidTo(LocalDateTime.now());
        newUser.setUserRole(ROLE_NON_PREMIUM);
        appUserRepository.save(newUser);
        log.info("New user created with email: {}", email);
    }

    private Optional<OidcUser> getLoggedInOidcUser() {
        Authentication authentication = getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof DefaultOidcUser oidcUser) {
            return Optional.of(oidcUser);
        } else {
            log.warn("Authentication object is null or not of type DefaultOidcUser!");
            return Optional.empty();
        }
    }

    private void setUserRoleAfterAuth() {
        Optional<AppUser> userOptional = getAuthenticatedUser();
        if (userOptional.isEmpty()) {
            log.error(NO_DB_ENTRY_ERR_MSG);
            return;
        }
        AppUser user = userOptional.get();
        updateRoleBasedOnPremiumValidToTimestamp(user);
        updateSecurityContextWithNewRole(user);
    }

    private void updateRoleBasedOnPremiumValidToTimestamp(AppUser user) {
        String newRole = determineUserRole(user);
        user.setUserRole(newRole);
        appUserRepository.save(user);
    }

    private String determineUserRole(AppUser user) {
        LocalDateTime premiumValidTo = user.getPremiumValidTo();
        boolean isPremiumValid = premiumValidTo.isAfter(LocalDateTime.now());
        if (isPremiumValid) {
            return ROLE_PREMIUM;
        } else {
            return ROLE_NON_PREMIUM;
        }
    }

    private void updateSecurityContextWithNewRole(AppUser user) {
        Authentication auth = getContext().getAuthentication();
        List<SimpleGrantedAuthority> updatedAuthorities = singletonList(new SimpleGrantedAuthority(user.getUserRole()));
        var newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        getContext().setAuthentication(newAuth);
    }
}
