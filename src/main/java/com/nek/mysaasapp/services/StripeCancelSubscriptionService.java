package com.nek.mysaasapp.services;

import static com.stripe.model.Subscription.retrieve;

import com.nek.mysaasapp.config.CustomProperties;
import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.entities.AppUser;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCancelParams;

import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeCancelSubscriptionService {

    @NonNull
    private final StripeProperties stripeProperties;
    @NonNull
    private final CustomProperties customProperties;
    public static final String RETRIEVE_SUB_ERR_MSG =
        "An error occurred when trying to retrieve subscription by its id, aborting";
    public static final String CANCEL_SUB_INFO_MSG = "Successfully canceled subscription with id {}";
    public static final String CANCEL_SUB_ERR_MSG = "An error occurred when trying to cancel subscription";
    public static final String PAYMENT_DISABLED_AND_NO_ID_INFO_MSG =
        "User has no subscription id and payments are disabled in config, no need to cancel subscription";

    /**
     * Cancels the subscription of the given user.
     *
     * @param appUser the user whose subscription should be canceled
     */
    public void cancelSubscription(@NonNull AppUser appUser) {
        if (customProperties.isRemovePayment() && appUser.getSubscriptionId() == null) {
            log.info(PAYMENT_DISABLED_AND_NO_ID_INFO_MSG);
            return;
        }
        Stripe.apiKey = stripeProperties.getApiKey();
        Subscription resource;
        try {
            resource = retrieve(appUser.getSubscriptionId());
        } catch (StripeException e) {
            log.error(RETRIEVE_SUB_ERR_MSG, e);
            return;
        }
        SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
        try {
            Subscription subscription = resource.cancel(params);
            log.info(CANCEL_SUB_INFO_MSG, subscription.getId());
        } catch (StripeException e) {
            log.error(CANCEL_SUB_ERR_MSG, e);
        }
    }
}
