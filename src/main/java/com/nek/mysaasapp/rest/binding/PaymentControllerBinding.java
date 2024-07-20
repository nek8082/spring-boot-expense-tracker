package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentControllerBinding {

    public static final String CHECKOUT_URL = "/checkout";
    public static final String CHECKOUT_SUCCESS_URL = "/checkout-success";
    public static final String WEBHOOK_URL = "/webhook";
}
