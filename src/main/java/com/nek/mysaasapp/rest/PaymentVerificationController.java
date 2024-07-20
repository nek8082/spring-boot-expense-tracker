package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.PaymentVerificationControllerBinding.APPLE_PAYMENT_VERIFICATION_URL;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@code PaymentVerificationController} provides an endpoint to serve the Apple Developer Merchant ID Domain Association file
 * required by Apple to validate the merchant's domain for Apple Pay transactions, which can be enabled in Stripe.
 */
@RestController
@RequiredArgsConstructor
public class PaymentVerificationController {

    @NonNull
    private final ResourceLoader resourceLoader;

    @GetMapping(APPLE_PAYMENT_VERIFICATION_URL)
    public ResponseEntity<Resource> apple() {
        Resource resource =
            resourceLoader.getResource("classpath:static/.well-known/apple-developer-merchantid-domain-association");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(resource);
    }

}
