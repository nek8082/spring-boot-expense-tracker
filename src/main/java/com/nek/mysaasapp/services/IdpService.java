package com.nek.mysaasapp.services;

import lombok.NonNull;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface IdpService {

    /**
     * Deletes an OIDC user from the IDP user management system.
     * This method retrieves an access token and uses it to request the deletion of a user from an IDP.
     * Logs the response status code upon successful deletion or logs an error if the deletion fails.
     *
     * @param user The OIDCUser to delete from IDP, not null.
     */
    void deleteUserFromIdp(@NonNull OidcUser user);
}
