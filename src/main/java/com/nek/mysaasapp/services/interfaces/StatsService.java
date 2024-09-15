package com.nek.mysaasapp.services.interfaces;

import java.time.Month;
import java.time.Year;
import java.util.Map;

import com.nek.mysaasapp.model.MonthlyStats;

import lombok.NonNull;

public interface StatsService {

    /**
     * Calculate monthly statistics for the given year.
     *
     * @param year the year for which to calculate the statistics
     * @return a map of monthly statistics
     */
    public Map<Month, MonthlyStats> calculateMonthlyStats(@NonNull Year year);
}
