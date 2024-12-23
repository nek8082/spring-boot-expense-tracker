package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.DashboardControllerBinding.PREMIUM_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PRIVATE_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.ROOT_URL;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_PREMIUM;
import static com.nek.mysaasapp.services.SpringSecurityBasedUserService.ROLE_VERIFIED;

import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.services.interfaces.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @NonNull
    private final UserService springSecurityBasedUserService;

    @GetMapping(ROOT_URL)
    public String rootEndpoint() {
        return "redirect:" + PUBLIC_URL;
    }

    @GetMapping(PUBLIC_URL)
    public String publicEndpoint() {
        Optional<AppUser> user = springSecurityBasedUserService.getAuthenticatedUser();
        if (user.isEmpty()) {
            return "public";
        } else if (ROLE_VERIFIED.equals(user.get().getUserRole())) {
            return "redirect:" + PRIVATE_URL;
        } else if (ROLE_PREMIUM.equals(user.get().getUserRole())) {
            return "redirect:" + PREMIUM_URL;
        }
        return "public";
    }

    @GetMapping(PRIVATE_URL)
    public String privateEndpoint() {
        return "private";
    }
}
