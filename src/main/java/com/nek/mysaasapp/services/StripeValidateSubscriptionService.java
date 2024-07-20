package com.nek.mysaasapp.services;

import static com.nek.mysaasapp.services.StripeCheckoutService.METADATA_KEY_EMAIL;
import static com.nek.mysaasapp.services.UserService.ROLE_PREMIUM;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDateTime;
import java.util.Optional;

import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.repository.AppUserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeValidateSubscriptionService {

    @NonNull
    private final AppUserRepository appUserRepository;
    private final StripeProperties stripeProperties;

    public static final String DESERIALIZATION_FAILED_MESSAGE = "Could not deserialize event: ";
    public static final String PAYMENT_SUCCEEDED_MESSAGE = "Payment succeeded, handling premium timestamp.";
    public static final String UNHANDLED_EVENT_TYPE_MESSAGE = "Unhandled event type: ";
    public static final String USER_NOT_IN_DB_WARNING = "Received checkout.session.completed event message for user: {}, "
        + "which has no db entry. Not handling event any further.";
    public static final String SUBSCRIPTION_IS_NULL_ERROR_MESSAGE =
        "Subscription object is null, can't update premium status" + " for user";
    public static final String UNSUPPORTED_STRIPE_OBJECT_ERROR_MESSAGE = "Unsupported StripeObject type: {}";
    public static final String SUBSCRIPTION_ID_IS_NULL_ERROR_MESSAGE = "Subscription ID is null";
    public static final String STRIPE_EXCEPTION_MESSAGE = "An exception occurred when retrieving subscription";
    public static final String UPDATING_ROLE_PREMIUM_MESSAGE = "Saving user with updated role {} in DB";
    public static final String UPDATING_ROLE_PREMIUM_SECURITY_CONTEXT_MESSAGE =
        "Settings user role in the security context to {}";
    public static final String INVOICE_DEBUG_LOG_MSG = "Invoice: {}";
    public static final String SESSION_DEBUG_LOG = "Session: {}";

    /**
     * Handles incoming Stripe events by deserializing the nested object and performing
     * actions based on the event type.
     *
     * @param event the Stripe event object received from the webhook.
     * @return a {@link ResponseEntity<String>} indicating the result of the event handling.
     *         Returns OK (200) if the event is handled successfully, otherwise returns BAD REQUEST (400).
     */
    public ResponseEntity<String> handleStripeEvent(@NonNull Event event) {

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            return badRequest().body(DESERIALIZATION_FAILED_MESSAGE + event);
        }

        // Handle the event
        switch (event.getType()) {
            case "invoice.payment_succeeded", "checkout.session.completed", "invoice.paid":
                log.info(PAYMENT_SUCCEEDED_MESSAGE);
                updatePremiumStatusAfterPayment(stripeObject);
                return ok(PAYMENT_SUCCEEDED_MESSAGE);
            default:
                log.info(UNHANDLED_EVENT_TYPE_MESSAGE + "{}", event.getType());
                return badRequest().body(UNHANDLED_EVENT_TYPE_MESSAGE);
        }
    }

    /**
     * Updates a user's premium status following a successful payment.
     * This method handles Stripe objects received after successful payment transactions.
     * It listens for two types of objects:
     * Invoice: This object is used when an existing subscription has been paid.
     * Session: This object is used when a first-time subscription has been paid.
     * Listening to both objects ensures that all pathways through which a user can become premium are covered.
     */
    private void updatePremiumStatusAfterPayment(StripeObject stripeObject) {
        String email;
        String subscriptionId;
        Stripe.apiKey = stripeProperties.getApiKey();
        if (stripeObject instanceof Invoice invoice) {
            log.debug(INVOICE_DEBUG_LOG_MSG, invoice);
            Subscription subscription = fetchSubscription(invoice.getSubscription());
            if (subscription == null) {
                log.error(SUBSCRIPTION_IS_NULL_ERROR_MESSAGE);
                return;
            }
            email = subscription.getMetadata().get(METADATA_KEY_EMAIL);
            subscriptionId = subscription.getId();
        } else if (stripeObject instanceof Session session) {
            log.debug(SESSION_DEBUG_LOG, session);
            email = session.getMetadata().get(METADATA_KEY_EMAIL);
            subscriptionId = session.getSubscription();
        } else {
            log.error(UNSUPPORTED_STRIPE_OBJECT_ERROR_MESSAGE, stripeObject.getClass().getSimpleName());
            return;
        }
        updateUserPremiumStatus(email, subscriptionId);
    }

    private Subscription fetchSubscription(String subscriptionId) {
        if (subscriptionId == null) {
            log.error(SUBSCRIPTION_ID_IS_NULL_ERROR_MESSAGE);
            return null;
        }
        try {
            return Subscription.retrieve(subscriptionId);
        } catch (StripeException e) {
            log.error(STRIPE_EXCEPTION_MESSAGE + ": ", e);
            return null;
        }
    }

    private void updateUserPremiumStatus(String email, String subscriptionId) {
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn(USER_NOT_IN_DB_WARNING, email);
            return;
        }

        log.info(UPDATING_ROLE_PREMIUM_MESSAGE, ROLE_PREMIUM);
        AppUser appUser = user.get();
        appUser.setPremiumValidTo(LocalDateTime.now().plusDays(31));
        appUser.setUserRole(ROLE_PREMIUM);
        appUser.setSubscriptionId(subscriptionId);
        appUserRepository.save(appUser);

        log.info(UPDATING_ROLE_PREMIUM_SECURITY_CONTEXT_MESSAGE, ROLE_PREMIUM);
    }
}
