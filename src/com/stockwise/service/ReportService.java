package com.stockwise.service;

import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import com.stockwise.model.HistoricalDemand;
import com.stockwise.model.Inventory;
import com.stockwise.model.Prediction;
import com.stockwise.model.PredictionStatus;
import com.stockwise.model.StockHistory;
import com.stockwise.model.StockStatus;
import com.stockwise.model.Student;
import com.stockwise.repository.DemandRepository;
import com.stockwise.repository.HistoricalDemandRepository;
import com.stockwise.repository.InventoryRepository;
import com.stockwise.repository.StockHistoryRepository;
import com.stockwise.repository.StudentRepository;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Service consolidating application-wide statistics, dashboard metrics, and analytical reports.
 * All metrics are dynamically computed from live repository state.
 */
public class ReportService {

    /**
     * Snapshot of real-time application metrics for dashboard display.
     */
    public static class DashboardMetrics {
        public final int totalItems;
        public final int totalAvailableStock;
        public final double totalInventoryValuation;
        public final int totalStudents;
        public final int totalPendingDemands;
        public final int totalIssuedDemands;
        public final int totalRejectedDemands;
        public final int currentMonthIssuedUnits;
        public final int lowStockCount;
        public final int outOfStockCount;
        public final int highDemandCount;
        public final List<StockHistory> recentTransactions;
        public final List<String> highDemandItemNames;

        public DashboardMetrics(int totalItems, int totalAvailableStock, double totalInventoryValuation,
                                int totalStudents, int totalPendingDemands, int totalIssuedDemands,
                                int totalRejectedDemands, int currentMonthIssuedUnits,
                                int lowStockCount, int outOfStockCount, int highDemandCount,
                                List<StockHistory> recentTransactions, List<String> highDemandItemNames) {
            this.totalItems = totalItems;
            this.totalAvailableStock = totalAvailableStock;
            this.totalInventoryValuation = totalInventoryValuation;
            this.totalStudents = totalStudents;
            this.totalPendingDemands = totalPendingDemands;
            this.totalIssuedDemands = totalIssuedDemands;
            this.totalRejectedDemands = totalRejectedDemands;
            this.currentMonthIssuedUnits = currentMonthIssuedUnits;
            this.lowStockCount = lowStockCount;
            this.outOfStockCount = outOfStockCount;
            this.highDemandCount = highDemandCount;
            this.recentTransactions = recentTransactions;
            this.highDemandItemNames = highDemandItemNames;
        }
    }

    /**
     * DTO for monthly aggregated demand.
     */
    public static class MonthlyDemandAggregate {
        public final YearMonth yearMonth;
        public final int issuedDemandUnits;
        public final int historicalDemandUnits;
        public final int totalConsumedUnits;

        public MonthlyDemandAggregate(YearMonth yearMonth, int issuedDemandUnits, int historicalDemandUnits) {
            this.yearMonth = yearMonth;
            this.issuedDemandUnits = issuedDemandUnits;
            this.historicalDemandUnits = historicalDemandUnits;
            this.totalConsumedUnits = issuedDemandUnits + historicalDemandUnits;
        }
    }

    private final InventoryRepository inventoryRepository;
    private final StudentRepository studentRepository;
    private final DemandRepository demandRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final HistoricalDemandRepository historicalDemandRepository;
    private final PredictionService predictionService;

    public ReportService(InventoryRepository inventoryRepository,
                         StudentRepository studentRepository,
                         DemandRepository demandRepository,
                         StockHistoryRepository stockHistoryRepository,
                         HistoricalDemandRepository historicalDemandRepository,
                         PredictionService predictionService) {
        this.inventoryRepository = inventoryRepository;
        this.studentRepository = studentRepository;
        this.demandRepository = demandRepository;
        this.stockHistoryRepository = stockHistoryRepository;
        this.historicalDemandRepository = historicalDemandRepository;
        this.predictionService = predictionService;
    }

    /**
     * Computes live dashboard metrics with zero hardcoding.
     */
    public DashboardMetrics getDashboardMetrics() {
        List<Inventory> items = inventoryRepository.findAll();
        int totalItems = items.size();
        int totalAvailableStock = items.stream().mapToInt(Inventory::getAvailableQuantity).sum();
        double totalValuation = items.stream().mapToDouble(i -> i.getAvailableQuantity() * i.getPrice()).sum();

        int lowStockCount = (int) items.stream().filter(i -> i.getStockStatus() == StockStatus.LOW_STOCK).count();
        int outOfStockCount = (int) items.stream().filter(i -> i.getStockStatus() == StockStatus.OUT_OF_STOCK).count();

        int totalStudents = studentRepository.count();

        int pendingDemands = demandRepository.countByStatus(DemandStatus.PENDING);
        int issuedDemands = demandRepository.countByStatus(DemandStatus.ISSUED);
        int rejectedDemands = demandRepository.countByStatus(DemandStatus.REJECTED);

        YearMonth currentYm = YearMonth.now();
        int currentMonthIssued = demandRepository.findByStatus(DemandStatus.ISSUED).stream()
                .filter(d -> YearMonth.from(d.getRequestDate()).equals(currentYm))
                .mapToInt(DemandRecord::getRequestedQuantity)
                .sum();

        List<StockHistory> recentTxns = stockHistoryRepository.findRecent(6);

        // Calculate high-demand items for next month
        YearMonth nextMonth = currentYm.plusMonths(1);
        List<String> highDemandNames = new ArrayList<>();
        int highDemandCount = 0;

        for (Inventory item : items) {
            Prediction pred = predictionService.predictDemandForItem(item.getItemId(), nextMonth);
            if (pred.getStatus() == PredictionStatus.SUFFICIENT && pred.isHighDemand()) {
                highDemandCount++;
                highDemandNames.add(item.getItemName() + " (" + item.getItemId() + "): Pred " +
                        String.format("%.1f", pred.getPredictedDemand()) + " > Stock " + item.getAvailableQuantity());
            }
        }

        return new DashboardMetrics(
                totalItems,
                totalAvailableStock,
                totalValuation,
                totalStudents,
                pendingDemands,
                issuedDemands,
                rejectedDemands,
                currentMonthIssued,
                lowStockCount,
                outOfStockCount,
                highDemandCount,
                recentTxns,
                highDemandNames
        );
    }

    /**
     * Aggregates demand history by calendar month.
     */
    public List<MonthlyDemandAggregate> getMonthlyDemandSummary() {
        Map<YearMonth, Integer> issuedMap = new TreeMap<>();
        for (DemandRecord d : demandRepository.findByStatus(DemandStatus.ISSUED)) {
            YearMonth ym = YearMonth.from(d.getRequestDate());
            issuedMap.put(ym, issuedMap.getOrDefault(ym, 0) + d.getRequestedQuantity());
        }

        Map<YearMonth, Integer> histMap = new TreeMap<>();
        for (HistoricalDemand hd : historicalDemandRepository.findAll()) {
            histMap.put(hd.getYearMonth(), histMap.getOrDefault(hd.getYearMonth(), 0) + hd.getConsumedQuantity());
        }

        // Combine all distinct YearMonths
        Map<YearMonth, MonthlyDemandAggregate> aggregateMap = new TreeMap<>(Collections.reverseOrder());
        for (YearMonth ym : issuedMap.keySet()) {
            int issued = issuedMap.get(ym);
            int hist = histMap.getOrDefault(ym, 0);
            aggregateMap.put(ym, new MonthlyDemandAggregate(ym, issued, hist));
        }
        for (YearMonth ym : histMap.keySet()) {
            if (!aggregateMap.containsKey(ym)) {
                int hist = histMap.get(ym);
                int issued = issuedMap.getOrDefault(ym, 0);
                aggregateMap.put(ym, new MonthlyDemandAggregate(ym, issued, hist));
            }
        }

        return new ArrayList<>(aggregateMap.values());
    }
}
