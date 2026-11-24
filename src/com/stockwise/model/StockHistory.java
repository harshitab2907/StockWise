package com.stockwise.model;

import com.stockwise.exception.ValidationException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Audit log recording all inventory changes and movements.
 * Assigned to: Anmol / Janvi (Stock Tracking)
 */
public class StockHistory {
    private String historyId;
    private String itemId;
    private TransactionType transactionType;
    private int quantityChanged;
    private LocalDate transactionDate;
    private String remarks;

    public StockHistory(String historyId, String itemId, TransactionType transactionType,
                        int quantityChanged, LocalDate transactionDate, String remarks) {
        validateHistoryId(historyId);
        validateItemId(itemId);
        if (transactionType == null) {
            throw new ValidationException("Transaction type cannot be null.");
        }

        this.historyId = historyId.trim();
        this.itemId = itemId.trim();
        this.transactionType = transactionType;
        this.quantityChanged = quantityChanged;
        this.transactionDate = (transactionDate != null) ? transactionDate : LocalDate.now();
        this.remarks = (remarks == null) ? "" : remarks.trim();
    }

    public static void validateHistoryId(String historyId) {
        if (historyId == null || historyId.trim().isEmpty()) {
            throw new ValidationException("History ID cannot be empty.");
        }
    }

    public static void validateItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID cannot be empty.");
        }
    }

    // Getters and Setters

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        validateHistoryId(historyId);
        this.historyId = historyId.trim();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        validateItemId(itemId);
        this.itemId = itemId.trim();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new ValidationException("Transaction type cannot be null.");
        }
        this.transactionType = transactionType;
    }

    public int getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(int quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = (transactionDate != null) ? transactionDate : LocalDate.now();
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
        StockHistory that = (StockHistory) o;
        return Objects.equals(historyId, that.historyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(historyId);
    }

    @Override
    public String toString() {
        return "StockHistory{" +
                "historyId='" + historyId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", transactionType=" + transactionType +
                ", quantityChanged=" + quantityChanged +
                ", transactionDate=" + transactionDate +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
