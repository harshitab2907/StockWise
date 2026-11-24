package com.stockwise.exception;

/**
 * Thrown when a requested resource (item, student, demand) does not exist.
 */
public class ResourceNotFoundException extends StockWiseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
