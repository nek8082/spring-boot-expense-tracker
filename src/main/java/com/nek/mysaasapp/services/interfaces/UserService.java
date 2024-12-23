package com.nek.mysaasapp.services.interfaces;

import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;

public interface UserService {
    String ROLE_NON_PREMIUM = "ROLE_NON_PREMIUM";
    String ROLE_PREMIUM = "ROLE_PREMIUM";
    String NO_DB_ENTRY_ERR_MSG = "User has no db entry, can not set user role";

    /**
     * This method is called after the user has been authenticated.
     * It checks if the user is already present in the database and if not, it creates a new entry.
     * It also sets the user role based on the premium valid to timestamp.
     */
    void setupUser();

    /**
     * This method is called when the user logs out.
     * It deletes the user from Auth0 and the database.
     */
    void deleteUser();

    /**
     * This method returns the authenticated user.
     *
     * @return the authenticated user
     */
    Optional<AppUser> getAuthenticatedUser();
}
