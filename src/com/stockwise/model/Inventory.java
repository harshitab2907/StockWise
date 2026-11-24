package com.stockwise.model;

import com.stockwise.exception.ValidationException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an item in the college inventory.
 * Assigned to: Anmol (Inventory Management)
 */
public class Inventory {
    private String itemId;
    private String itemName;
    private String category;
    private String description;
    private int availableQuantity;
    private double price;
    private int minimumStock;
    private LocalDate dateAdded;

    public Inventory(String itemId, String itemName, String category, String description,
                     int availableQuantity, double price, int minimumStock, LocalDate dateAdded) {
        validateItemId(itemId);
        validateItemName(itemName);
        validateQuantity(availableQuantity);
        validatePrice(price);
        validateMinimumStock(minimumStock);

        this.itemId = itemId.trim();
        this.itemName = itemName.trim();
        this.category = (category == null || category.trim().isEmpty()) ? "General" : category.trim();
        this.description = (description == null) ? "" : description.trim();
        this.availableQuantity = availableQuantity;
        this.price = price;
        this.minimumStock = minimumStock;
        this.dateAdded = (dateAdded != null) ? dateAdded : LocalDate.now();
    }

    public static void validateItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID cannot be empty.");
        }
    }

    public static void validateItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new ValidationException("Item name cannot be empty.");
        }
    }

    public static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new ValidationException("Available quantity cannot be negative: " + quantity);
        }
    }

    public static void validatePrice(double price) {
        if (price < 0.0) {
            throw new ValidationException("Price cannot be negative: " + price);
        }
    }

    public static void validateMinimumStock(int minimumStock) {
        if (minimumStock < 0) {
            throw new ValidationException("Minimum stock threshold cannot be negative: " + minimumStock);
        }
    }

    public StockStatus getStockStatus() {
        return StockStatus.computeStatus(this.availableQuantity, this.minimumStock);
    }

    // Getters and Setters with validation

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        validateItemId(itemId);
        this.itemId = itemId.trim();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        validateItemName(itemName);
        this.itemName = itemName.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category == null || category.trim().isEmpty()) ? "General" : category.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        validateQuantity(availableQuantity);
        this.availableQuantity = availableQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        validatePrice(price);
        this.price = price;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        validateMinimumStock(minimumStock);
        this.minimumStock = minimumStock;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = (dateAdded != null) ? dateAdded : LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(itemId, inventory.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", availableQuantity=" + availableQuantity +
                ", price=" + price +
                ", minimumStock=" + minimumStock +
                ", status=" + getStockStatus() +
                '}';
    }
}
