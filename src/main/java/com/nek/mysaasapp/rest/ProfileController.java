package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.model.ThymeleafConstants.EMAIL_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.MANAGE_URL_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.PREMIUM_VALID_TO_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.rest.binding.ProfileControllerBinding.PROFILE_URL;
import static com.nek.mysaasapp.util.DateFormatter.formatLocalDateTime;

import java.util.Optional;

import com.nek.mysaasapp.config.StripeProperties;
import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.services.SpringSecurityBasedUserService;

import com.nek.mysaasapp.services.interfaces.UserService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    @NonNull
    private final UserService springSecurityBasedUserService;
    @NonNull
    private final StripeProperties stripeProperties;

    @GetMapping(PROFILE_URL)
    public String profileEndpoint(Model model) {
        Optional<AppUser> user = springSecurityBasedUserService.getAuthenticatedUser();
        if (user.isEmpty()) {
            log.warn("No authenticated user found!");
            return "redirect:" + PUBLIC_URL;
        }
        model.addAttribute(EMAIL_THYMELEAF_MODEL_ATTRIBUTE_NAME, user.get().getEmail());
        model.addAttribute(PREMIUM_VALID_TO_THYMELEAF_MODEL_ATTRIBUTE_NAME, formatLocalDateTime(user.get().getPremiumValidTo()));
        model.addAttribute(MANAGE_URL_THYMELEAF_MODEL_ATTRIBUTE_NAME, stripeProperties.getManageSubscriptionUrl());
        return "profile/profile";
    }

    @PostMapping(PROFILE_URL)
    public String deleteProfileEndpoint(HttpServletRequest request) {
        springSecurityBasedUserService.deleteUser();
        new SecurityContextLogoutHandler().logout(request, null, null);
        return "redirect:" + PUBLIC_URL;
    }
}
