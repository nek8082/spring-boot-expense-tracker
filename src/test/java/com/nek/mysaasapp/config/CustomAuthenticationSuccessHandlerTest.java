package com.nek.mysaasapp.config;

import com.nek.mysaasapp.repository.AppUserRepository;
import com.nek.mysaasapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationSuccessHandlerTest {
    @Mock
    private UserService userService;
    @Mock
    private AppUserRepository userRepository;
    @Mock
    private CustomProperties customProperties;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private CustomAuthenticationSuccessHandler successHandler;

    @Test
    void onAuthenticationSuccess_roleIsPremium_redirectToCheckoutSuccess() throws IOException {
        // Arrange
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_PREMIUM");
        Collection<? extends GrantedAuthority> authorities = List.of(authority);
        https://stackoverflow.com/questions/7366237/mockito-stubbing-methods-that-return-type-with-bounded-wild-cards
        doReturn(authorities).when(authentication).getAuthorities();

        when(customProperties.isRemovePayment()).thenReturn(true);
        when(customProperties.isRemovePayment()).thenReturn(false);

        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(userService).setupUser();
        verify(response).sendRedirect("/checkout-success");
    }

    @Test
    void onAuthenticationSuccess() {
    }
}