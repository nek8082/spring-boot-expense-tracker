package com.nek.mysaasapp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import lombok.NonNull;

public class DateFormatter {

    /**
     * Formats a {@link LocalDateTime} object to a string in the format "dd. MMMM yyyy, HH:mm 'Uhr'".
     *
     * @param dateTime the {@link LocalDateTime} object to format
     * @return the formatted string
     */
    public static String formatLocalDateTime(@NonNull LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy, HH:mm 'Uhr'", Locale.GERMANY);
        return dateTime.format(formatter);
    }
}
