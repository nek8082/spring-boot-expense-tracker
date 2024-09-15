package com.nek.mysaasapp.services;

import com.nek.mysaasapp.model.MonthlyStats;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import lombok.NonNull;

import java.time.Month;
import java.time.Year;
import java.util.Map;

public interface StatsService {

    /**
     * Calculate monthly statistics for the given year.
     *
     * @param year the year for which to calculate the statistics
     * @return a map of monthly statistics
     */
    public Map<Month, MonthlyStats> calculateMonthlyStats(@NonNull Year year);
}
