package com.stockwise.util;

import com.stockwise.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Common validation helpers across modules.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s()]{7,20}$");

    private ValidationUtils() {}

    public static String requireNonEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
        return str.trim();
    }

    public static int requireNonNegative(int val, String fieldName) {
        if (val < 0) {
            throw new ValidationException(fieldName + " cannot be negative: " + val);
        }
        return val;
    }

    public static double requireNonNegative(double val, String fieldName) {
        if (val < 0.0) {
            throw new ValidationException(fieldName + " cannot be negative: " + val);
        }
        return val;
    }

    public static int requirePositive(int val, String fieldName) {
        if (val <= 0) {
            throw new ValidationException(fieldName + " must be strictly positive: " + val);
        }
        return val;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
}
