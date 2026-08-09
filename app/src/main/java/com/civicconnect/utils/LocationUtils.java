package com.civicconnect.utils;

import java.util.Locale;

/**
 * Java utility class for calculating distances between GPS coordinates using the Haversine formula.
 * Used across CivicConnect to sort and display complaint proximity to users.
 */
public final class LocationUtils {

    private static final double EARTH_RADIUS_KM = 6371.0008;

    private LocationUtils() {
        // Prevent instantiation
    }

    /**
     * Calculates distance between two (latitude, longitude) coordinate pairs in kilometers.
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Formats distance into human-readable string (e.g. "350 m away" or "2.4 km away").
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) Math.round(distanceKm * 1000);
            return String.format(Locale.getDefault(), "%d m away", meters);
        } else {
            return String.format(Locale.getDefault(), "%.1f km away", distanceKm);
        }
    }
}
