package com.stockwise.util;

import com.stockwise.exception.ValidationException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date and YearMonth formatting and parsing utility.
 */
public class DateUtils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter MONTH_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");
    public static final DateTimeFormatter MONTH_ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private DateUtils() {}

    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DATE_FORMATTER);
    }

    public static String formatYearMonth(YearMonth yearMonth) {
        if (yearMonth == null) return "N/A";
        return yearMonth.format(MONTH_DISPLAY_FORMATTER);
    }

    public static String formatYearMonthIso(YearMonth yearMonth) {
        if (yearMonth == null) return "N/A";
        return yearMonth.format(MONTH_ISO_FORMATTER);
    }

    /**
     * Parses year-month strings such as '2026-08' or '08/2026'.
     */
    public static YearMonth parseYearMonth(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Month input cannot be empty.");
        }
        String trimmed = input.trim();
        try {
            if (trimmed.contains("-")) {
                return YearMonth.parse(trimmed, MONTH_ISO_FORMATTER);
            } else if (trimmed.contains("/")) {
                String[] parts = trimmed.split("/");
                if (parts.length == 2) {
                    int month = Integer.parseInt(parts[0].trim());
                    int year = Integer.parseInt(parts[1].trim());
                    return YearMonth.of(year, month);
                }
            }
            return YearMonth.parse(trimmed);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new ValidationException("Invalid month format: '" + input + "'. Expected format: YYYY-MM (e.g. 2026-08) or MM/YYYY.");
        }
    }
}
