package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.EditBalanceControllerBinding.UPDATE_INITIAL_BALANCE_URL;

import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.repository.AppUserRepository;
import com.nek.mysaasapp.services.interfaces.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EditBalanceController {

    @NonNull
    private final UserService springSecurityBasedUserService;
    @NonNull
    private final AppUserRepository userRepository;

    @GetMapping(UPDATE_INITIAL_BALANCE_URL)
    public String getInitialBalance() {
        return "editbalance/edit-balance";
    }

    @PostMapping(UPDATE_INITIAL_BALANCE_URL)
    public String updateInitialBalance(@RequestParam("initialBalance") double initialBalance) {
        Optional<AppUser> authenticatedUser = springSecurityBasedUserService.getAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            AppUser appUser = authenticatedUser.get();
            appUser.setInitialBalance(initialBalance);
            userRepository.save(appUser);
        }
        return "redirect:/premium";
    }
}
