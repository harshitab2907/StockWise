package com.stockwise.exception;

/**
 * Thrown when an inventory deduction exceeds available stock.
 */
public class InsufficientStockException extends StockWiseException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
