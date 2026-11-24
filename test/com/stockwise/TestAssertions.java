package com.stockwise;

import java.util.Objects;

/**
 * Lightweight, zero-dependency assertion framework for automated tests.
 */
public class TestAssertions {

    public static class AssertionFailureException extends RuntimeException {
        public AssertionFailureException(String message) {
            super(message);
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected TRUE but was FALSE)");
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected FALSE but was TRUE)");
        }
    }

    public static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    public static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected: " + expected + ", actual: " + actual + ", delta: " + delta + ")");
        }
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    public static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected NOT NULL but was NULL)");
        }
    }

    public static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected NULL but was: " + obj + ")");
        }
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable executable, String message) {
        try {
            executable.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return expectedType.cast(actual);
            }
            throw new AssertionFailureException("Assertion Failed: " + message + " (expected exception of type " +
                    expectedType.getSimpleName() + " but caught " + actual.getClass().getSimpleName() + ": " + actual.getMessage() + ")");
        }
        throw new AssertionFailureException("Assertion Failed: " + message + " (expected exception of type " +
                expectedType.getSimpleName() + " but no exception was thrown)");
    }
}
