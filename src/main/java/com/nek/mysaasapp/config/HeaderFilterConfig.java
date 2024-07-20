package com.nek.mysaasapp.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Configuration class that registers the ForwardedHeaderFilter bean.
 *
 * <p>The ForwardedHeaderFilter processes forwarded headers (X-Forwarded-* headers)
 * set by the nginx reverse proxy to relay information about the original request.
 * This filter ensures that the correct values for client-relevant information such as
 * the original IP address, the original protocol (HTTP/HTTPS), and the original host
 * are reflected in the HttpServletRequest.
 *
 * <p>Without this filter, the application would redirect to
 * {@code http://<host>/oauth2/authorization/keycloak} instead of
 * {@code https://<host>/oauth2/authorization/keycloak} during the OIDC flow.
 */
@Configuration
public class HeaderFilterConfig {

    /**
     * Registers the ForwardedHeaderFilter to process forwarded headers.
     *
     * @return the FilterRegistrationBean configured with ForwardedHeaderFilter
     */
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new ForwardedHeaderFilter());
        filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegBean;
    }
}
