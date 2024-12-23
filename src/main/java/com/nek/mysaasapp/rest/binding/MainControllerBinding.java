package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainControllerBinding {

    public static final String ROOT_URL = "/";
    public static final String PUBLIC_URL = "/public";
    public static final String PRIVATE_URL = "/private";
}
