package com.stockwise.model;

/**
 * Represents the lifecycle status of a student demand request.
 * Assigned to: Sedhat (Demand Management)
 */
public enum DemandStatus {
    PENDING("Pending"),
    ISSUED("Issued"),
    REJECTED("Rejected");

    private final String displayName;

    DemandStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
