package com.nek.mysaasapp.services.interfaces;

import static com.stripe.param.checkout.SessionCreateParams.Mode.SUBSCRIPTION;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.entities.AppUser;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
