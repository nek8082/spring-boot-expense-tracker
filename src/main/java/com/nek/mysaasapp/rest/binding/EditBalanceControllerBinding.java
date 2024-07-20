package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EditBalanceControllerBinding {

    public static final String UPDATE_INITIAL_BALANCE_URL = "/updateInitialBalance";
}
