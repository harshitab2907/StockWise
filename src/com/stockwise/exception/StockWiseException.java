package com.stockwise.exception;

/**
 * Base unchecked exception for StockWise application.
 */
public class StockWiseException extends RuntimeException {
    public StockWiseException(String message) {
        super(message);
    }

    public StockWiseException(String message, Throwable cause) {
        super(message, cause);
    }
}
