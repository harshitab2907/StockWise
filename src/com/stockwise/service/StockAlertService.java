package com.stockwise.service;

import com.stockwise.model.Inventory;
import com.stockwise.model.Prediction;
import com.stockwise.model.PredictionStatus;
import com.stockwise.model.StockStatus;
import com.stockwise.repository.InventoryRepository;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing stock health alerts: low stock, stockouts, high demand, and suggested restock quantities.
 * Assigned to: Anmol (Stock Health & Alerts)
 */
public class StockAlertService {

    /**
     * DTO representing an integrated stock alert item.
     */
    public static class StockAlertItem {
        private final String itemId;
        private final String itemName;
        private final String category;
        private final int currentStock;
        private final int minimumStock;
        private final StockStatus stockStatus;
        private final Double predictedDemand;
        private final Integer suggestedRestock;
        private final boolean lowStock;
        private final boolean outOfStock;
        private final boolean highDemand;
        private final String alertMessage;

        public StockAlertItem(String itemId, String itemName, String category, int currentStock,
                              int minimumStock, StockStatus stockStatus, Double predictedDemand,
                              Integer suggestedRestock, boolean lowStock, boolean outOfStock,
                              boolean highDemand, String alertMessage) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.category = category;
            this.currentStock = currentStock;
            this.minimumStock = minimumStock;
            this.stockStatus = stockStatus;
            this.predictedDemand = predictedDemand;
            this.suggestedRestock = suggestedRestock;
            this.lowStock = lowStock;
            this.outOfStock = outOfStock;
            this.highDemand = highDemand;
            this.alertMessage = alertMessage;
        }

        public String getItemId() { return itemId; }
        public String getItemName() { return itemName; }
        public String getCategory() { return category; }
        public int getCurrentStock() { return currentStock; }
        public int getMinimumStock() { return minimumStock; }
        public StockStatus getStockStatus() { return stockStatus; }
        public Double getPredictedDemand() { return predictedDemand; }
        public Integer getSuggestedRestock() { return suggestedRestock; }
        public boolean isLowStock() { return lowStock; }
        public boolean isOutOfStock() { return outOfStock; }
        public boolean isHighDemand() { return highDemand; }
        public String getAlertMessage() { return alertMessage; }
    }

    private final InventoryRepository inventoryRepository;
    private final PredictionService predictionService;

    public StockAlertService(InventoryRepository inventoryRepository, PredictionService predictionService) {
        this.inventoryRepository = inventoryRepository;
        this.predictionService = predictionService;
    }

    /**
     * Generates a comprehensive list of all stock alerts across the inventory.
     */
    public List<StockAlertItem> generateAllAlerts() {
        List<Inventory> allItems = inventoryRepository.findAll();
        List<StockAlertItem> alerts = new ArrayList<>();
        YearMonth targetMonth = YearMonth.now().plusMonths(1);

        for (Inventory item : allItems) {
            StockStatus status = item.getStockStatus();
            boolean isOut = (status == StockStatus.OUT_OF_STOCK);
            boolean isLow = (status == StockStatus.LOW_STOCK);

            // Fetch or calculate prediction
            Optional<Prediction> predOpt = predictionService.getLatestPredictionForItem(item.getItemId());
            Prediction pred = predOpt.orElseGet(() -> predictionService.predictDemandForItem(item.getItemId(), targetMonth));

            Double predictedDemand = (pred.getStatus() == PredictionStatus.SUFFICIENT) ? pred.getPredictedDemand() : null;
            Integer suggestedRestock = (pred.getStatus() == PredictionStatus.SUFFICIENT) ? pred.getSuggestedRestockQuantity() : null;
            boolean isHighDemand = (predictedDemand != null && predictedDemand > item.getAvailableQuantity());

            if (isOut || isLow || isHighDemand || (suggestedRestock != null && suggestedRestock > 0)) {
                List<String> messages = new ArrayList<>();
                if (isOut) messages.add("CRITICAL: Out of stock (0 units remaining)");
                if (isLow) messages.add("WARNING: Low stock (" + item.getAvailableQuantity() + " <= threshold of " + item.getMinimumStock() + ")");
                if (isHighDemand) messages.add(String.format("HIGH DEMAND: Predicted (%.1f) exceeds stock (%d)", predictedDemand, item.getAvailableQuantity()));
                if (suggestedRestock != null && suggestedRestock > 0) messages.add("RESTOCK: Order " + suggestedRestock + " units");

                alerts.add(new StockAlertItem(
                        item.getItemId(),
                        item.getItemName(),
                        item.getCategory(),
                        item.getAvailableQuantity(),
                        item.getMinimumStock(),
                        status,
                        predictedDemand,
                        suggestedRestock,
                        isLow,
                        isOut,
                        isHighDemand,
                        String.join(" | ", messages)
                ));
            }
        }

        return alerts;
    }

    public List<StockAlertItem> getLowStockAlerts() {
        return generateAllAlerts().stream().filter(StockAlertItem::isLowStock).collect(Collectors.toList());
    }

    public List<StockAlertItem> getOutOfStockAlerts() {
        return generateAllAlerts().stream().filter(StockAlertItem::isOutOfStock).collect(Collectors.toList());
    }

    public List<StockAlertItem> getHighDemandAlerts() {
        return generateAllAlerts().stream().filter(StockAlertItem::isHighDemand).collect(Collectors.toList());
    }
}
