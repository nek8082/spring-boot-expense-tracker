package com.nek.mysaasapp.services;

import com.nek.mysaasapp.services.interfaces.IdpService;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.nek.mysaasapp.config.KeycloakProperties;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for interacting with Keycloak to manage user authentication and management.
 * This service provides functionality to delete users from Keycloak
 * and to retrieve access tokens for interacting with the Keycloak Management API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService implements IdpService {

    @NonNull
    private final KeycloakProperties keycloakProperties;

    public static final String DELETE_USER_KEYCLOAK_ERR_MSG = "Something went wrong when trying to delete user {} from keycloak:";

    @Override
    public void deleteUserFromIdp(@NonNull OidcUser user) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(keycloakProperties.getAuthServerUrl())
            .realm(keycloakProperties.getRealm())
            .clientId(keycloakProperties.getClientId())
            .clientSecret(keycloakProperties.getClientSecret())
            .username(keycloakProperties.getTechnicalUser().getUsername())
            .password(keycloakProperties.getTechnicalUser().getPassword())
            .grantType(OAuth2Constants.PASSWORD)
            .build();

            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();
            Response response = usersResource.delete(user.getAttribute("sub"));
            log.info("User {} deleted from keycloak with status code {}", user, response.getStatus());
        } catch (Exception e) {
            log.error(DELETE_USER_KEYCLOAK_ERR_MSG, user, e);
        }
    }
}
