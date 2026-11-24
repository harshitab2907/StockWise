package com.stockwise.service;

import com.stockwise.exception.DuplicateResourceException;
import com.stockwise.exception.ResourceNotFoundException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.Inventory;
import com.stockwise.model.StockHistory;
import com.stockwise.model.StockStatus;
import com.stockwise.model.TransactionType;
import com.stockwise.repository.DemandRepository;
import com.stockwise.repository.InventoryRepository;
import com.stockwise.repository.StockHistoryRepository;
import com.stockwise.util.IDGenerator;
import com.stockwise.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing inventory operations, stock adjustments, and history tracking.
 * Assigned to: Anmol (Inventory Management)
 */
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final DemandRepository demandRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            StockHistoryRepository stockHistoryRepository,
                            DemandRepository demandRepository) {
        this.inventoryRepository = inventoryRepository;
        this.stockHistoryRepository = stockHistoryRepository;
        this.demandRepository = demandRepository;
    }

    /**
     * Adds a new inventory item and logs initial stock in stock history if quantity > 0.
     */
    public synchronized Inventory addItem(String itemId, String itemName, String category,
                                          String description, int availableQuantity,
                                          double price, int minimumStock) {
        String finalId = (itemId == null || itemId.trim().isEmpty())
                ? IDGenerator.generateItemId()
                : itemId.trim();

        if (inventoryRepository.existsById(finalId)) {
            throw new DuplicateResourceException("An item with ID '" + finalId + "' already exists.");
        }

        Inventory item = new Inventory(finalId, itemName, category, description,
                availableQuantity, price, minimumStock, LocalDate.now());
        inventoryRepository.save(item);

        // Record initial stock creation in history
        if (availableQuantity > 0) {
            StockHistory history = new StockHistory(
                    IDGenerator.generateHistoryId(),
                    finalId,
                    TransactionType.STOCK_ADDED,
                    availableQuantity,
                    LocalDate.now(),
                    "Initial inventory stock allocation"
            );
            stockHistoryRepository.save(history);
        }

        return item;
    }

    public List<Inventory> getAllItems() {
        return inventoryRepository.findAll();
    }

    public Inventory getItemById(String itemId) {
        ValidationUtils.requireNonEmpty(itemId, "Item ID");
        return inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with ID '" + itemId + "' not found."));
    }

    public List<Inventory> searchByName(String name) {
        return inventoryRepository.findByNameContaining(name);
    }

    public List<Inventory> searchByCategory(String category) {
        return inventoryRepository.findByCategory(category);
    }

    /**
     * Updates item details (name, category, description, price, minimum stock).
     */
    public synchronized Inventory updateItem(String itemId, String newName, String newCategory,
                                             String newDescription, Double newPrice, Integer newMinStock) {
        Inventory item = getItemById(itemId);

        if (newName != null && !newName.trim().isEmpty()) {
            item.setItemName(newName);
        }
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            item.setCategory(newCategory);
        }
        if (newDescription != null) {
            item.setDescription(newDescription);
        }
        if (newPrice != null) {
            item.setPrice(newPrice);
        }
        if (newMinStock != null) {
            item.setMinimumStock(newMinStock);
        }

        return inventoryRepository.save(item);
    }

    /**
     * Deletes an item only if no dependent demand records exist.
     */
    public synchronized boolean deleteItem(String itemId) {
        Inventory item = getItemById(itemId);

        // Academic business rule: prevent deletion if demand records reference this item
        int dependentDemands = demandRepository.findByItemId(itemId).size();
        if (dependentDemands > 0) {
            throw new ValidationException("Cannot delete item '" + item.getItemName() + "' (" + itemId +
                    "): " + dependentDemands + " dependent demand records exist.");
        }

        return inventoryRepository.deleteById(itemId);
    }

    /**
     * Increases inventory stock and records a STOCK_ADDED transaction.
     */
    public synchronized Inventory addStock(String itemId, int quantityToAdd, String remarks) {
        ValidationUtils.requirePositive(quantityToAdd, "Quantity to add");
        Inventory item = getItemById(itemId);

        int updatedQty = item.getAvailableQuantity() + quantityToAdd;
        item.setAvailableQuantity(updatedQty);
        inventoryRepository.save(item);

        StockHistory history = new StockHistory(
                IDGenerator.generateHistoryId(),
                itemId,
                TransactionType.STOCK_ADDED,
                quantityToAdd,
                LocalDate.now(),
                (remarks == null || remarks.trim().isEmpty()) ? "Stock replenishment" : remarks.trim()
        );
        stockHistoryRepository.save(history);

        return item;
    }

    /**
     * Adjusts inventory stock to an absolute new quantity and records STOCK_ADJUSTED transaction.
     */
    public synchronized Inventory adjustStock(String itemId, int newQuantity, String reason) {
        ValidationUtils.requireNonNegative(newQuantity, "New quantity");
        ValidationUtils.requireNonEmpty(reason, "Adjustment reason");

        Inventory item = getItemById(itemId);
        int currentQty = item.getAvailableQuantity();
        int delta = newQuantity - currentQty;

        if (delta == 0) {
            return item; // no adjustment needed
        }

        item.setAvailableQuantity(newQuantity);
        inventoryRepository.save(item);

        StockHistory history = new StockHistory(
                IDGenerator.generateHistoryId(),
                itemId,
                TransactionType.STOCK_ADJUSTED,
                delta,
                LocalDate.now(),
                "Stock adjusted: " + reason.trim() + " (was " + currentQty + ", now " + newQuantity + ")"
        );
        stockHistoryRepository.save(history);

        return item;
    }

    // Status queries

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getStockStatus() == StockStatus.LOW_STOCK)
                .collect(Collectors.toList());
    }

    public List<Inventory> getOutOfStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getStockStatus() == StockStatus.OUT_OF_STOCK)
                .collect(Collectors.toList());
    }

    public List<Inventory> getInStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getStockStatus() == StockStatus.IN_STOCK)
                .collect(Collectors.toList());
    }

    public int getTotalItemCount() {
        return inventoryRepository.count();
    }

    public int getTotalAvailableUnits() {
        return inventoryRepository.findAll().stream()
                .mapToInt(Inventory::getAvailableQuantity)
                .sum();
    }

    public double getTotalInventoryValuation() {
        return inventoryRepository.findAll().stream()
                .mapToDouble(i -> i.getAvailableQuantity() * i.getPrice())
                .sum();
    }
}
