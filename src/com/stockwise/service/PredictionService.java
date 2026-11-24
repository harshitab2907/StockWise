package com.stockwise.service;

import com.stockwise.exception.DuplicateResourceException;
import com.stockwise.exception.ResourceNotFoundException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import com.stockwise.model.HistoricalDemand;
import com.stockwise.model.Inventory;
import com.stockwise.model.Prediction;
import com.stockwise.model.Prediction.MonthConsumption;
import com.stockwise.model.PredictionStatus;
import com.stockwise.model.StockHistory;
import com.stockwise.model.TransactionType;
import com.stockwise.repository.DemandRepository;
import com.stockwise.repository.HistoricalDemandRepository;
import com.stockwise.repository.InventoryRepository;
import com.stockwise.repository.PredictionRepository;
import com.stockwise.repository.StockHistoryRepository;
import com.stockwise.util.DateUtils;
import com.stockwise.util.IDGenerator;
import com.stockwise.util.ValidationUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Intelligent demand prediction service implementing explainable 3-month moving average forecasting.
 * Enforces strict historical data completeness and safe restocking calculations.
 * Assigned to: Harshita (Demand Prediction)
 */
public class PredictionService {

    private final InventoryRepository inventoryRepository;
    private final DemandRepository demandRepository;
    private final HistoricalDemandRepository historicalDemandRepository;
    private final PredictionRepository predictionRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public PredictionService(InventoryRepository inventoryRepository,
                             DemandRepository demandRepository,
                             HistoricalDemandRepository historicalDemandRepository,
                             PredictionRepository predictionRepository,
                             StockHistoryRepository stockHistoryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.demandRepository = demandRepository;
        this.historicalDemandRepository = historicalDemandRepository;
        this.predictionRepository = predictionRepository;
        this.stockHistoryRepository = stockHistoryRepository;
    }

    /**
     * Records an explicit monthly historical consumption for an item.
     * Does NOT mutate active stock or create student demands.
     */
    public synchronized HistoricalDemand recordHistoricalDemand(String itemId, int year, int month,
                                                                int consumedQuantity, String remarks) {
        ValidationUtils.requireNonEmpty(itemId, "Item ID");
        ValidationUtils.requireNonNegative(consumedQuantity, "Historical consumed quantity");

        if (month < 1 || month > 12) {
            throw new ValidationException("Invalid month: " + month + ". Must be between 1 and 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new ValidationException("Invalid year: " + year + ". Must be between 2000 and 2100.");
        }

        // Verify item exists
        Inventory item = inventoryRepository.findById(itemId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with ID '" + itemId + "' not found."));

        YearMonth targetYm = YearMonth.of(year, month);

        if (historicalDemandRepository.existsByItemIdAndYearMonth(item.getItemId(), targetYm)) {
            throw new DuplicateResourceException("Historical demand for item '" + item.getItemName() +
                    "' (" + itemId + ") for period " + DateUtils.formatYearMonth(targetYm) + " has already been recorded.");
        }

        String id = IDGenerator.generateHistoricalDemandId();
        HistoricalDemand hd = new HistoricalDemand(id, item.getItemId(), targetYm, consumedQuantity, remarks);
        historicalDemandRepository.save(hd);

        // Audit log in stock history (quantityChanged = 0 so active stock is unchanged)
        StockHistory history = new StockHistory(
                IDGenerator.generateHistoryId(),
                item.getItemId(),
                TransactionType.HISTORICAL_ENTRY,
                0,
                LocalDate.now(),
                "Historical demand recorded for " + DateUtils.formatYearMonth(targetYm) +
                        ": " + consumedQuantity + " units (No active stock modified)"
        );
        stockHistoryRepository.save(history);

        return hd;
    }

    /**
     * Evaluates monthly consumption and coverage for a specific YearMonth.
     * Combines verified historical records and actual ISSUED demands.
     * Excludes PENDING and REJECTED demands.
     */
    public MonthConsumption evaluateMonthCoverage(String itemId, YearMonth ym) {
        Optional<HistoricalDemand> hdOpt = historicalDemandRepository.findByItemIdAndYearMonth(itemId, ym);

        List<DemandRecord> issuedDemands = demandRepository.findByItemIdAndStatus(itemId, DemandStatus.ISSUED)
                .stream()
                .filter(d -> YearMonth.from(d.getRequestDate()).equals(ym))
                .collect(Collectors.toList());

        int issuedSum = issuedDemands.stream().mapToInt(DemandRecord::getRequestedQuantity).sum();

        if (hdOpt.isPresent() && !issuedDemands.isEmpty()) {
            int total = hdOpt.get().getConsumedQuantity() + issuedSum;
            return new MonthConsumption(ym, total, true, "Combined: Historical (" +
                    hdOpt.get().getConsumedQuantity() + ") + Issued Demands (" + issuedSum + ")");
        } else if (hdOpt.isPresent()) {
            int qty = hdOpt.get().getConsumedQuantity();
            String desc = (qty == 0) ? "Explicit Zero Historical Demand" : "Historical Record";
            return new MonthConsumption(ym, qty, true, desc);
        } else if (!issuedDemands.isEmpty()) {
            return new MonthConsumption(ym, issuedSum, true, "Issued Student Demands (" + issuedDemands.size() + " records)");
        } else {
            return new MonthConsumption(ym, 0, false, "No Coverage (Missing Historical Data)");
        }
    }

    /**
     * Generates a 3-month moving average prediction for the specified item and target month.
     * If targetMonth is null, defaults to next calendar month.
     */
    public synchronized Prediction predictDemandForItem(String itemId, YearMonth targetMonth) {
        ValidationUtils.requireNonEmpty(itemId, "Item ID");
        Inventory item = inventoryRepository.findById(itemId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with ID '" + itemId + "' not found."));

        YearMonth target = (targetMonth != null) ? targetMonth : YearMonth.now().plusMonths(1);

        // Previous three completed calendar months: T-3, T-2, T-1
        YearMonth m1 = target.minusMonths(3);
        YearMonth m2 = target.minusMonths(2);
        YearMonth m3 = target.minusMonths(1);

        MonthConsumption mc1 = evaluateMonthCoverage(item.getItemId(), m1);
        MonthConsumption mc2 = evaluateMonthCoverage(item.getItemId(), m2);
        MonthConsumption mc3 = evaluateMonthCoverage(item.getItemId(), m3);

        List<MonthConsumption> referenceMonths = new ArrayList<>();
        referenceMonths.add(mc1);
        referenceMonths.add(mc2);
        referenceMonths.add(mc3);

        boolean allCovered = mc1.isCovered() && mc2.isCovered() && mc3.isCovered();

        Prediction prediction;
        String predId = IDGenerator.generatePredictionId();

        if (!allCovered) {
            List<String> missing = new ArrayList<>();
            if (!mc1.isCovered()) missing.add(DateUtils.formatYearMonth(m1));
            if (!mc2.isCovered()) missing.add(DateUtils.formatYearMonth(m2));
            if (!mc3.isCovered()) missing.add(DateUtils.formatYearMonth(m3));

            String explanation = "INSUFFICIENT HISTORY: Missing coverage for " + String.join(", ", missing) +
                    ". Accurate 3-month moving average requires complete data for all 3 previous months.";

            prediction = new Prediction(
                    predId,
                    item.getItemId(),
                    target,
                    0.0,
                    0,
                    PredictionStatus.INSUFFICIENT_HISTORY,
                    false,
                    referenceMonths,
                    explanation
            );
        } else {
            double predictedDemand = (mc1.getConsumedQuantity() + mc2.getConsumedQuantity() + mc3.getConsumedQuantity()) / 3.0;
            int safetyStock = item.getMinimumStock();
            int currentStock = item.getAvailableQuantity();

            // Formula: MAX(0, ceil(Predicted Demand + Safety Stock - Current Stock))
            int suggestedRestock = Math.max(0, (int) Math.ceil(predictedDemand + safetyStock - currentStock));
            boolean isHighDemand = predictedDemand > currentStock;

            String explanation = String.format(
                    "SUFFICIENT HISTORY: Average of [%s: %d, %s: %d, %s: %d] = %.2f units. " +
                            "Safety Stock: %d, Available: %d -> Suggested Restock: %d units.",
                    DateUtils.formatYearMonth(m1), mc1.getConsumedQuantity(),
                    DateUtils.formatYearMonth(m2), mc2.getConsumedQuantity(),
                    DateUtils.formatYearMonth(m3), mc3.getConsumedQuantity(),
                    predictedDemand, safetyStock, currentStock, suggestedRestock
            );

            prediction = new Prediction(
                    predId,
                    item.getItemId(),
                    target,
                    predictedDemand,
                    suggestedRestock,
                    PredictionStatus.SUFFICIENT,
                    isHighDemand,
                    referenceMonths,
                    explanation
            );
        }

        predictionRepository.save(prediction);
        return prediction;
    }

    /**
     * Runs demand prediction for all items in inventory for the target month.
     */
    public synchronized List<Prediction> predictForAllItems(YearMonth targetMonth) {
        YearMonth target = (targetMonth != null) ? targetMonth : YearMonth.now().plusMonths(1);
        List<Inventory> allItems = inventoryRepository.findAll();
        List<Prediction> predictions = new ArrayList<>();
        for (Inventory item : allItems) {
            predictions.add(predictDemandForItem(item.getItemId(), target));
        }
        return predictions;
    }

    public List<HistoricalDemand> getHistoricalDemandForItem(String itemId) {
        return historicalDemandRepository.findByItemId(itemId);
    }

    public List<HistoricalDemand> getAllHistoricalDemands() {
        return historicalDemandRepository.findAll();
    }

    public List<Prediction> getPredictionHistory() {
        return predictionRepository.findAll();
    }

    public List<Prediction> getPredictionHistoryForItem(String itemId) {
        return predictionRepository.findByItemId(itemId);
    }

    public Optional<Prediction> getLatestPredictionForItem(String itemId) {
        return predictionRepository.findLatestByItemId(itemId);
    }
}
