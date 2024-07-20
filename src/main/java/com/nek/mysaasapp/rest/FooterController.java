package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.CONTACT_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.IMPRINT_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.PRIVACY_URL;
import static com.nek.mysaasapp.rest.binding.FooterControllerBinding.TERMS_OF_USE_URL;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FooterController {

    @GetMapping(CONTACT_URL)
    public String contactEndpoint() {
        return "footer/contact";
    }

    @GetMapping(PRIVACY_URL)
    public String privateEndpoint() {
        return "footer/privacy";
    }

    @GetMapping(TERMS_OF_USE_URL)
    public String termsOfUseEndpoint(Model model) {
        return "footer/terms-of-use";
    }

    @GetMapping(IMPRINT_URL)
    public String imprintEndpoint() {
        return "footer/imprint";
    }
}
