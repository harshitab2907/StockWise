package com.stockwise.model;

import com.stockwise.exception.ValidationException;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Represents verified past monthly consumption for an inventory item.
 * Used exclusively for demand forecasting without mutating active stock levels.
 * Assigned to: Harshita (Demand Prediction)
 */
public class HistoricalDemand {
    private String id;
    private String itemId;
    private YearMonth yearMonth;
    private int consumedQuantity;
    private String remarks;

    public HistoricalDemand(String id, String itemId, YearMonth yearMonth, int consumedQuantity, String remarks) {
        validateId(id);
        validateItemId(itemId);
        validateYearMonth(yearMonth);
        validateConsumedQuantity(consumedQuantity);

        this.id = id.trim();
        this.itemId = itemId.trim();
        this.yearMonth = yearMonth;
        this.consumedQuantity = consumedQuantity;
        this.remarks = (remarks == null) ? "Historical demand record" : remarks.trim();
    }

    public static void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Historical Demand ID cannot be empty.");
        }
    }

    public static void validateItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID cannot be empty.");
        }
    }

    public static void validateYearMonth(YearMonth ym) {
        if (ym == null) {
            throw new ValidationException("Historical YearMonth cannot be null.");
        }
    }

    public static void validateConsumedQuantity(int quantity) {
        if (quantity < 0) {
            throw new ValidationException("Historical consumed quantity cannot be negative: " + quantity);
        }
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        validateId(id);
        this.id = id.trim();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        validateItemId(itemId);
        this.itemId = itemId.trim();
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        validateYearMonth(yearMonth);
        this.yearMonth = yearMonth;
    }

    public int getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(int consumedQuantity) {
        validateConsumedQuantity(consumedQuantity);
        this.consumedQuantity = consumedQuantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = (remarks == null) ? "" : remarks.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricalDemand that = (HistoricalDemand) o;
        return Objects.equals(itemId, that.itemId) && Objects.equals(yearMonth, that.yearMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, yearMonth);
    }

    @Override
    public String toString() {
        return "HistoricalDemand{" +
                "id='" + id + '\'' +
                ", itemId='" + itemId + '\'' +
                ", yearMonth=" + yearMonth +
                ", consumedQuantity=" + consumedQuantity +
                '}';
    }
}
