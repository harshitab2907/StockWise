package com.stockwise;

import com.stockwise.model.Inventory;
import com.stockwise.model.Student;
import com.stockwise.model.TransactionType;
import com.stockwise.repository.InMemoryDemandRepository;
import com.stockwise.repository.InMemoryHistoricalDemandRepository;
import com.stockwise.repository.InMemoryInventoryRepository;
import com.stockwise.repository.InMemoryPredictionRepository;
import com.stockwise.repository.InMemoryStockHistoryRepository;
import com.stockwise.repository.InMemoryStudentRepository;
import com.stockwise.service.DemandService;
import com.stockwise.service.InventoryService;
import com.stockwise.service.PredictionService;

import java.time.LocalDate;

import static com.stockwise.TestAssertions.*;

/**
 * Unit tests verifying Module D: Stock History and Audit Tracking.
 */
public class StockHistoryTest {

    private InMemoryInventoryRepository inventoryRepo;
    private InMemoryStockHistoryRepository stockHistoryRepo;
    private InMemoryDemandRepository demandRepo;
    private InMemoryStudentRepository studentRepo;
    private InMemoryHistoricalDemandRepository historicalDemandRepo;
    private InMemoryPredictionRepository predictionRepo;

    private InventoryService inventoryService;
    private DemandService demandService;
    private PredictionService predictionService;

    public void setUp() {
        inventoryRepo = new InMemoryInventoryRepository();
        stockHistoryRepo = new InMemoryStockHistoryRepository();
        demandRepo = new InMemoryDemandRepository();
        studentRepo = new InMemoryStudentRepository();
        historicalDemandRepo = new InMemoryHistoricalDemandRepository();
        predictionRepo = new InMemoryPredictionRepository();

        inventoryService = new InventoryService(inventoryRepo, stockHistoryRepo, demandRepo);
        demandService = new DemandService(demandRepo, inventoryRepo, studentRepo, stockHistoryRepo);
        predictionService = new PredictionService(inventoryRepo, demandRepo, historicalDemandRepo, predictionRepo, stockHistoryRepo);

        studentRepo.save(new Student("STU-1", "Test Student", "R1", "CS", "test@college.edu", "9876543210"));
    }

    public void testStockAdditionsCreateHistory() {
        setUp();
        Inventory item = inventoryService.addItem("ITM-HIST1", "Item One", "General", "", 10, 50.0, 5);
        assertEquals(1, stockHistoryRepo.count(), "Initial stock creation creates history");

        inventoryService.addStock("ITM-HIST1", 15, "Replenishment batch A");
        assertEquals(2, stockHistoryRepo.count(), "Add stock creates history");

        var latest = stockHistoryRepo.findAll().get(0); // latest first
        assertEquals(TransactionType.STOCK_ADDED, latest.getTransactionType(), "Transaction is STOCK_ADDED");
        assertEquals(15, latest.getQuantityChanged(), "Quantity changed is +15");
        assertEquals(25, item.getAvailableQuantity(), "Stock is 25");
    }

    public void testStockIssuanceCreatesHistory() {
        setUp();
        inventoryService.addItem("ITM-HIST2", "Item Two", "General", "", 30, 20.0, 5);
        var demand = demandService.submitDemand("STU-1", "ITM-HIST2", 10, "Class experiment");
        demandService.issueDemand(demand.getDemandId(), "Approved");

        var latest = stockHistoryRepo.findAll().get(0);
        assertEquals(TransactionType.STOCK_ISSUED, latest.getTransactionType(), "Transaction is STOCK_ISSUED");
        assertEquals(-10, latest.getQuantityChanged(), "Quantity changed is -10");
    }

    public void testHistoricalDemandEntriesDoNotModifyCurrentStock() {
        setUp();
        Inventory item = inventoryService.addItem("ITM-HIST3", "Item Three", "General", "", 50, 100.0, 10);
        assertEquals(50, item.getAvailableQuantity(), "Initial stock is 50");

        // Record historical demand for past month
        predictionService.recordHistoricalDemand("ITM-HIST3", 2026, 7, 35, "Historical summer demand");

        // Stock MUST remain exactly 50!
        Inventory itemAfter = inventoryRepo.findById("ITM-HIST3").orElseThrow();
        assertEquals(50, itemAfter.getAvailableQuantity(), "Current stock must NOT be modified by historical demand entry");

        // Historical demand repo must have 1 entry
        assertEquals(1, historicalDemandRepo.count(), "Historical demand stored");
    }
}
