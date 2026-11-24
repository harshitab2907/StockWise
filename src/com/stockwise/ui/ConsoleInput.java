package com.stockwise.ui;

import com.stockwise.util.DateUtils;

import java.time.YearMonth;
import java.util.Scanner;

/**
 * Resilient console input wrapper preventing malformed input exceptions.
 */
public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public ConsoleInput() {
        this(new Scanner(System.in));
    }

    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
            System.out.println("  [!] Input cannot be empty. Please enter a valid value.");
        }
    }

    public String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]: " : ": "));
        String line = scanner.nextLine();
        if (line == null || line.trim().isEmpty()) {
            return defaultValue;
        }
        return line.trim();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please enter a valid integer.");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.println("  [!] Value must be between " + min + " and " + max + ".");
        }
    }

    public Integer readOptionalInt(String prompt, Integer defaultValue) {
        while (true) {
            System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]: " : ": "));
            String line = scanner.nextLine();
            if (line == null || line.trim().isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please enter a valid integer or leave empty.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                double val = Double.parseDouble(line.trim());
                if (val < 0.0) {
                    System.out.println("  [!] Value cannot be negative.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid decimal number. Please try again.");
            }
        }
    }

    public Double readOptionalDouble(String prompt, Double defaultValue) {
        while (true) {
            System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]: " : ": "));
            String line = scanner.nextLine();
            if (line == null || line.trim().isEmpty()) {
                return defaultValue;
            }
            try {
                double val = Double.parseDouble(line.trim());
                if (val < 0.0) {
                    System.out.println("  [!] Value cannot be negative.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please enter a valid decimal or leave empty.");
            }
        }
    }

    public YearMonth readYearMonth(String prompt, YearMonth defaultValue) {
        while (true) {
            String defaultStr = defaultValue != null ? DateUtils.formatYearMonthIso(defaultValue) : "current+1";
            System.out.print(prompt + " (YYYY-MM or press Enter for " + defaultStr + "): ");
            String line = scanner.nextLine();
            if (line == null || line.trim().isEmpty()) {
                return defaultValue != null ? defaultValue : YearMonth.now().plusMonths(1);
            }
            try {
                return DateUtils.parseYearMonth(line);
            } catch (Exception e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public boolean readConfirmation(String prompt) {
        System.out.print(prompt + " (y/N): ");
        String line = scanner.nextLine();
        return line != null && line.trim().equalsIgnoreCase("y");
    }

    public void waitForEnter() {
        System.out.print("\nPress [ENTER] to return to menu...");
        scanner.nextLine();
    }
}
