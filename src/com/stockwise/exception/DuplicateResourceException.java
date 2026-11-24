package com.stockwise.exception;

/**
 * Thrown when attempting to register an entity with an ID or unique key that already exists.
 */
public class DuplicateResourceException extends StockWiseException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
