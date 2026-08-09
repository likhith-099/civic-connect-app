package com.civicconnect.utils;

import java.util.regex.Pattern;

/**
 * Java utility class providing validation helpers for user inputs.
 * Demonstrates Java and Kotlin interoperability within the CivicConnect Android app.
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,13}$"
    );

    private ValidationUtils() {
        // Prevent instantiation
    }

    /**
     * Validates whether the given string is a valid email address.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Checks if the password satisfies the minimum length requirement.
     */
    public static boolean isValidPassword(String password, int minLength) {
        return password != null && password.trim().length() >= minLength;
    }

    /**
     * Validates phone numbers (10 to 13 digits with optional leading '+').
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Sanitizes raw text input to prevent XSS / malicious injection.
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("<[^>]*>", "");
    }
}
