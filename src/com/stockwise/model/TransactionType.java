package com.stockwise.model;

/**
 * Categorizes the types of stock movements in Stock History.
 * Assigned to: Anmol / Janvi (Stock Tracking)
 */
public enum TransactionType {
    STOCK_ADDED("Stock Added"),
    STOCK_ISSUED("Stock Issued"),
    STOCK_ADJUSTED("Stock Adjusted"),
    HISTORICAL_ENTRY("Historical Entry");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
