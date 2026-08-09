package com.civicconnect.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Java utility class for date and time formatting across the CivicConnect application.
 * Demonstrates Java helper integration into Kotlin ViewModels and Repositories.
 */
public final class DateTimeUtils {

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DISPLAY_FORMAT = "MMM dd, yyyy • hh:mm a";
    private static final String TIME_ONLY_FORMAT = "HH:mm";

    private DateTimeUtils() {
        // Prevent instantiation
    }

    /**
     * Gets current time in HH:mm format (e.g. for chat messages).
     */
    public static String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_ONLY_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Formats an ISO 8601 date string into a user-friendly display string.
     */
    public static String formatIsoToDisplay(String isoDateString) {
        if (isoDateString == null || isoDateString.trim().isEmpty()) {
            return "N/A";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(isoDateString);
            if (date == null) {
                return isoDateString;
            }

            SimpleDateFormat outputFormat = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return isoDateString;
        }
    }
}
