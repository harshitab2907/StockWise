package com.stockwise.model;

/**
 * Indicates whether sufficient historical data exists to compute a reliable demand forecast.
 * Assigned to: Harshita (Demand Prediction)
 */
public enum PredictionStatus {
    SUFFICIENT("Sufficient History"),
    INSUFFICIENT_HISTORY("Insufficient History");

    private final String displayName;

    PredictionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
