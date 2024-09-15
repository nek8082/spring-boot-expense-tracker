package com.nek.mysaasapp.services.interfaces;

import java.util.Optional;

import com.stripe.model.checkout.Session;

public interface StripeCheckoutService {

    String METADATA_KEY_EMAIL = "user_email";
    String NO_USER_ENTRY_WHILE_CHECKOUT_ERR_MSG = "No user entry exists in app user table for"
            + " authenticated user, while in checkout. This is not allowed to happen! Returning empty session";
    String CHECKOUT_SUCCESS_ENDPOINT_URL = "/checkout-success";
    String CHECKOUT_SESSION_ERR_MSG = "Something went wrong when trying to create a checkout session";

    /**
     * Builds a Stripe Checkout session for the authenticated user.
     *
     * @return an {@link Optional} of a {@link Session} object, if the session was
     *         successfully created. Otherwise, an empty {@link Optional} is
     *         returned.
     */
    Optional<Session> buildStripeCheckoutSession();
}
