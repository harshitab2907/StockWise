package com.stockwise.exception;

/**
 * Thrown when data validation fails.
 */
public class ValidationException extends StockWiseException {
    public ValidationException(String message) {
        super(message);
    }
}
