package com.stockwise.model;

/**
 * Represents the stock health status of an inventory item.
 * Assigned to: Anmol (Inventory Management)
 */
public enum StockStatus {
    IN_STOCK("In Stock"),
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock");

    private final String displayName;

    StockStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines the stock status based on available and minimum stock threshold.
     */
    public static StockStatus computeStatus(int availableQuantity, int minimumStock) {
        if (availableQuantity <= 0) {
            return OUT_OF_STOCK;
        } else if (availableQuantity <= minimumStock) {
            return LOW_STOCK;
        } else {
            return IN_STOCK;
        }
    }
}
