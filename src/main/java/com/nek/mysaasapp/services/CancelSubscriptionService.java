package com.nek.mysaasapp.services;

import com.nek.mysaasapp.entities.AppUser;
import lombok.NonNull;

public interface CancelSubscriptionService {

    /**
     * Cancels the subscription of the given user.
     *
     * @param appUser the user whose subscription should be canceled
     */
    void cancelSubscription(@NonNull AppUser appUser);
}
