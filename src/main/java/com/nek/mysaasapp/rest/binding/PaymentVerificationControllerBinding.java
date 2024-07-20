package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentVerificationControllerBinding {

    public static final String APPLE_PAYMENT_VERIFICATION_URL = "/.well-known/apple-developer-merchantid-domain-association";
}
