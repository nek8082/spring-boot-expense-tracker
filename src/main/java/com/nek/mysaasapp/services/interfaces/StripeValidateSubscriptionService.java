package com.nek.mysaasapp.services.interfaces;

import com.stripe.model.Event;

import org.springframework.http.ResponseEntity;

import lombok.NonNull;

public interface StripeValidateSubscriptionService {

    String DESERIALIZATION_FAILED_MESSAGE = "Could not deserialize event: ";
    String PAYMENT_SUCCEEDED_MESSAGE = "Payment succeeded, handling premium timestamp.";
    String UNHANDLED_EVENT_TYPE_MESSAGE = "Unhandled event type: ";
    String USER_NOT_IN_DB_WARNING = "Received checkout.session.completed event message for user: {}, "
        + "which has no db entry. Not handling event any further.";
    String SUBSCRIPTION_IS_NULL_ERROR_MESSAGE =
        "Subscription object is null, can't update premium status" + " for user";
    String UNSUPPORTED_STRIPE_OBJECT_ERROR_MESSAGE = "Unsupported StripeObject type: {}";
    String SUBSCRIPTION_ID_IS_NULL_ERROR_MESSAGE = "Subscription ID is null";
    String STRIPE_EXCEPTION_MESSAGE = "An exception occurred when retrieving subscription";
    String UPDATING_ROLE_PREMIUM_MESSAGE = "Saving user with updated role {} in DB";
    String UPDATING_ROLE_PREMIUM_SECURITY_CONTEXT_MESSAGE =
        "Settings user role in the security context to {}";
    String INVOICE_DEBUG_LOG_MSG = "Invoice: {}";
    String SESSION_DEBUG_LOG = "Session: {}";

    /**
     * Handles incoming Stripe events by deserializing the nested object and performing
     * actions based on the event type.
     *
     * @param event the Stripe event object received from the webhook.
     * @return a {@link ResponseEntity<String>} indicating the result of the event handling.
     *         Returns OK (200) if the event is handled successfully, otherwise returns BAD REQUEST (400).
     */
    ResponseEntity<String> handleStripeEvent(@NonNull Event event);
}
