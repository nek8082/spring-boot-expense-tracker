package com.nek.mysaasapp.services;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeCheckoutService {

    public static final String METADATA_KEY_EMAIL = "user_email";
    public static final String NO_USER_ENTRY_WHILE_CHECKOUT_ERR_MSG = "No user entry exists in app user table for"
            + " authenticated user, while in checkout. This is not allowed to happen! Returning empty session";
    public static final String CHECKOUT_SUCCESS_ENDPOINT_URL = "/checkout-success";
    public static final String CHECKOUT_SESSION_ERR_MSG = "Something went wrong when trying to create a checkout session";

    @NonNull
    private final StripeProperties stripeProperties;
    @NonNull
    private final UserService userService;

    /**
     * Builds a Stripe Checkout session for the authenticated user.
     *
     * @return an {@link Optional} of a {@link Session} object, if the session was
     *         successfully created. Otherwise, an empty {@link Optional} is
     *         returned.
     */
    public Optional<Session> buildStripeCheckoutSession() {
        Stripe.apiKey = stripeProperties.getApiKey();

        Optional<AppUser> user = userService.getAuthenticatedUser();
        if (user.isEmpty()) {
            log.error(NO_USER_ENTRY_WHILE_CHECKOUT_ERR_MSG);
            try {
                return Optional.of(Session.create(Map.of()));
            } catch (StripeException e) {
                log.error(CHECKOUT_SESSION_ERR_MSG, e);
                return Optional.empty();
            }
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put(METADATA_KEY_EMAIL, getUserEmail());

        SessionCreateParams params =
            SessionCreateParams.builder()
                               .setSuccessUrl(stripeProperties.getBaseUrl() + CHECKOUT_SUCCESS_ENDPOINT_URL)
                               .addLineItem(SessionCreateParams.LineItem.builder()
                                                                        .setPrice(stripeProperties.getPrice())
                                                                        .setQuantity(1L)
                                                                        .build())
                               .setAutomaticTax(SessionCreateParams.AutomaticTax.builder()
                                                                                .setEnabled(true) // Aktiviere automatische
                                                                                                  // Steuerberechnung
                                                                                .build())
                               .setMode(SUBSCRIPTION)
                               .setCustomerEmail(getUserEmail())
                               .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                                                                                        .putAllMetadata(metadata)
                                                                                        .setTrialPeriodDays(30L)
                                                                                        .build())
                               .putAllMetadata(metadata)
                               .build();

        try {
            return Optional.of(Session.create(params));
        } catch (StripeException e) {
            log.error(CHECKOUT_SESSION_ERR_MSG, e);
            return Optional.empty();
        }
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DefaultOidcUser user = (DefaultOidcUser) authentication.getPrincipal();
        return user.getEmail();
    }
}
