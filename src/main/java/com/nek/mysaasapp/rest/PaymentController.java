package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PRIVATE_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.VERIFY_EMAIL_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_SUCCESS_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.WEBHOOK_URL;
import static com.stripe.net.Webhook.constructEvent;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Optional;

import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.services.StripeCheckoutService;
import com.nek.mysaasapp.services.StripeValidateSubscriptionService;
import com.nek.mysaasapp.services.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final StripeProperties stripeProperties;
    private final StripeCheckoutService stripeCheckoutService;
    private final StripeValidateSubscriptionService subscriptionService;
    private final UserService userService;
    public static final String STRIPE_SIGNATURE_HEADER_NAME = "Stripe-Signature";

    @PostMapping(CHECKOUT_URL)
    public String checkout() throws StripeException {
        Optional<Session> session = stripeCheckoutService.buildStripeCheckoutSession();
        return session.map(value -> "redirect:" + value.getUrl()).orElse("redirect:" + PRIVATE_URL);
    }

    @GetMapping(CHECKOUT_SUCCESS_URL)
    public String checkoutSuccess() {
        userService.setupUser();
        return "redirect:" + "/premium";
    }

    @PostMapping(WEBHOOK_URL)
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
        @RequestHeader(STRIPE_SIGNATURE_HEADER_NAME) String sigHeader) {
        Event event;
        try {
            event = constructEvent(payload, sigHeader, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(BAD_REQUEST).build();
        }
        return subscriptionService.handleStripeEvent(event);
    }
}
