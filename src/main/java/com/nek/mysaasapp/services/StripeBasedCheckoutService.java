package com.nek.mysaasapp.services;

import static com.stripe.param.checkout.SessionCreateParams.Mode.SUBSCRIPTION;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.services.interfaces.StripeCheckoutService;
import com.nek.mysaasapp.services.interfaces.UserService;
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
public class StripeBasedCheckoutService implements StripeCheckoutService {
    
    @NonNull
    private final StripeProperties stripeProperties;
    @NonNull
    private final UserService springSecurityBasedUserService;
    
    @Override
    public Optional<Session> buildStripeCheckoutSession() {
        Stripe.apiKey = stripeProperties.getApiKey();

        Optional<AppUser> user = springSecurityBasedUserService.getAuthenticatedUser();
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
