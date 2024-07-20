package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FooterControllerBinding {

    public static final String CONTACT_URL = "/contact";
    public static final String PRIVACY_URL = "/privacy";
    public static final String TERMS_OF_USE_URL = "/terms-of-use";
    public static final String IMPRINT_URL = "/imprint";
}
