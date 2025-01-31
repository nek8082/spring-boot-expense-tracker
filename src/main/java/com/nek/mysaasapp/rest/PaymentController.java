package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PRIVATE_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_SUCCESS_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.CHECKOUT_URL;
import static com.nek.mysaasapp.rest.binding.PaymentControllerBinding.WEBHOOK_URL;
import static com.stripe.net.Webhook.constructEvent;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Optional;

import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.services.StripeBasedCheckoutService;
import com.nek.mysaasapp.services.StripeBasedValidateSubscriptionService;
import com.nek.mysaasapp.services.interfaces.UserService;
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
    private final StripeBasedCheckoutService stripeBasedCheckoutService;
    private final StripeBasedValidateSubscriptionService subscriptionService;
    private final UserService springSecurityBasedUserService;
    public static final String STRIPE_SIGNATURE_HEADER_NAME = "Stripe-Signature";

    @PostMapping(CHECKOUT_URL)
    public String checkout() throws StripeException {
        Optional<Session> session = stripeBasedCheckoutService.buildStripeCheckoutSession();
        return session.map(value -> "redirect:" + value.getUrl()).orElse("redirect:" + PUBLIC_URL);
    }

    @GetMapping(CHECKOUT_SUCCESS_URL)
    public String checkoutSuccess() {
        springSecurityBasedUserService.setupUser();
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
