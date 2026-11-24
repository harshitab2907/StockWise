package com.stockwise;

import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import com.stockwise.model.Inventory;
import com.stockwise.model.Prediction;
import com.stockwise.model.PredictionStatus;
import com.stockwise.repository.InMemoryDemandRepository;
import com.stockwise.repository.InMemoryHistoricalDemandRepository;
import com.stockwise.repository.InMemoryInventoryRepository;
import com.stockwise.repository.InMemoryPredictionRepository;
import com.stockwise.repository.InMemoryStockHistoryRepository;
import com.stockwise.service.PredictionService;

import java.time.LocalDate;
import java.time.YearMonth;

import static com.stockwise.TestAssertions.*;

/**
 * Unit tests verifying Module E: Demand Prediction.
 * Includes the mandatory academic test scenario specified in Section 10 of project requirements.
 */
public class PredictionServiceTest {

    private InMemoryInventoryRepository inventoryRepo;
    private InMemoryDemandRepository demandRepo;
    private InMemoryHistoricalDemandRepository historicalDemandRepo;
    private InMemoryPredictionRepository predictionRepo;
    private InMemoryStockHistoryRepository stockHistoryRepo;
    private PredictionService service;

    public void setUp() {
        inventoryRepo = new InMemoryInventoryRepository();
        demandRepo = new InMemoryDemandRepository();
        historicalDemandRepo = new InMemoryHistoricalDemandRepository();
        predictionRepo = new InMemoryPredictionRepository();
        stockHistoryRepo = new InMemoryStockHistoryRepository();
        service = new PredictionService(inventoryRepo, demandRepo, historicalDemandRepo, predictionRepo, stockHistoryRepo);
    }

    /**
     * EXACT ACADEMIC SPECIFICATION TEST:
     * Month 1 demand = 10
     * Month 2 demand = 20
     * Month 3 demand = 30
     * Expected average: (10 + 20 + 30) / 3 = 20
     * Current stock = 12
     * Minimum stock = 5
     * Expected restocking quantity: MAX(0, 20 + 5 - 12) = 13
     * Expected results:
     * - Predicted demand = 20.0
     * - Suggested restock = 13
     */
    public void testExactAcademicRequirementPrediction() {
        setUp();
        // 1. Create item with Current Stock = 12, Minimum Stock = 5
        Inventory item = new Inventory("ITM-EXACT", "Precision Sensor", "Electronics", "Test", 12, 250.0, 5, LocalDate.now());
        inventoryRepo.save(item);

        // Target prediction month: Dec 2026 (Month 1 = Sep 2026, Month 2 = Oct 2026, Month 3 = Nov 2026)
        YearMonth target = YearMonth.of(2026, 12);
        YearMonth m1 = target.minusMonths(3); // 2026-09
        YearMonth m2 = target.minusMonths(2); // 2026-10
        YearMonth m3 = target.minusMonths(1); // 2026-11

        service.recordHistoricalDemand("ITM-EXACT", m1.getYear(), m1.getMonthValue(), 10, "Month 1 baseline");
        service.recordHistoricalDemand("ITM-EXACT", m2.getYear(), m2.getMonthValue(), 20, "Month 2 baseline");
        service.recordHistoricalDemand("ITM-EXACT", m3.getYear(), m3.getMonthValue(), 30, "Month 3 baseline");

        // 2. Execute prediction
        Prediction prediction = service.predictDemandForItem("ITM-EXACT", target);

        // 3. Verify exact results
        assertNotNull(prediction, "Prediction must not be null");
        assertEquals(PredictionStatus.SUFFICIENT, prediction.getStatus(), "Status must be SUFFICIENT");
        assertEquals(20.0, prediction.getPredictedDemand(), 0.0001, "Expected average demand = 20.0");
        assertEquals(13, prediction.getSuggestedRestockQuantity(), "Expected suggested restock = MAX(0, 20 + 5 - 12) = 13");
        assertTrue(prediction.isHighDemand(), "Predicted demand (20.0) > Available stock (12), highDemand must be TRUE");
    }

    public void testExplicitZeroDemandMonthIsTreatedAsKnownZero() {
        setUp();
        Inventory item = new Inventory("ITM-ZERO", "Test Item", "General", "", 10, 100.0, 5, LocalDate.now());
        inventoryRepo.save(item);

        YearMonth target = YearMonth.of(2026, 12);
        YearMonth m1 = target.minusMonths(3);
        YearMonth m2 = target.minusMonths(2);
        YearMonth m3 = target.minusMonths(1);

        // Month 1 = 15, Month 2 = 0 (EXPLICIT RECORDED ZERO), Month 3 = 15
        service.recordHistoricalDemand("ITM-ZERO", m1.getYear(), m1.getMonthValue(), 15, "Consumption M1");
        service.recordHistoricalDemand("ITM-ZERO", m2.getYear(), m2.getMonthValue(), 0, "Known zero demand month (holidays)");
        service.recordHistoricalDemand("ITM-ZERO", m3.getYear(), m3.getMonthValue(), 15, "Consumption M3");

        Prediction prediction = service.predictDemandForItem("ITM-ZERO", target);

        // Must be SUFFICIENT because Month 2 is an explicit 0, not missing data
        assertEquals(PredictionStatus.SUFFICIENT, prediction.getStatus(), "Explicit zero month must count as valid coverage");
        // Average: (15 + 0 + 15) / 3 = 10.0
        assertEquals(10.0, prediction.getPredictedDemand(), 0.0001, "Expected average = 10.0");
        // Restock: MAX(0, ceil(10 + 5 - 10)) = 5
        assertEquals(5, prediction.getSuggestedRestockQuantity(), "Suggested restock = 5");
    }

    public void testMissingHistoricalMonthCausesInsufficientHistory() {
        setUp();
        Inventory item = new Inventory("ITM-MISSING", "Test Item", "General", "", 10, 100.0, 5, LocalDate.now());
        inventoryRepo.save(item);

        YearMonth target = YearMonth.of(2026, 12);
        YearMonth m1 = target.minusMonths(3);
        YearMonth m2 = target.minusMonths(2);
        // m3 (Month 3) is deliberately omitted!

        service.recordHistoricalDemand("ITM-MISSING", m1.getYear(), m1.getMonthValue(), 10, "M1");
        service.recordHistoricalDemand("ITM-MISSING", m2.getYear(), m2.getMonthValue(), 20, "M2");

        Prediction prediction = service.predictDemandForItem("ITM-MISSING", target);

        // Missing month must NOT be silently replaced with 0; status must be INSUFFICIENT_HISTORY
        assertEquals(PredictionStatus.INSUFFICIENT_HISTORY, prediction.getStatus(),
                "Missing historical month must mark prediction as INSUFFICIENT_HISTORY");
        assertEquals(0.0, prediction.getPredictedDemand(), 0.0001, "Predicted demand must be 0 when history insufficient");
    }

    public void testPendingAndRejectedDemandsAreExcluded() {
        setUp();
        Inventory item = new Inventory("ITM-EXCLUDE", "Test Item", "General", "", 10, 100.0, 5, LocalDate.now());
        inventoryRepo.save(item);

        YearMonth target = YearMonth.of(2026, 12);
        YearMonth m1 = target.minusMonths(3);
        YearMonth m2 = target.minusMonths(2);
        YearMonth m3 = target.minusMonths(1);

        // Add historical records for M1 and M2
        service.recordHistoricalDemand("ITM-EXCLUDE", m1.getYear(), m1.getMonthValue(), 10, "M1");
        service.recordHistoricalDemand("ITM-EXCLUDE", m2.getYear(), m2.getMonthValue(), 10, "M2");

        // In Month 3: Add ONLY a PENDING and a REJECTED demand record
        LocalDate d3 = m3.atDay(10);
        demandRepo.save(new DemandRecord("DMD-P", "STU-1", "ITM-EXCLUDE", 15, d3, DemandStatus.PENDING, "Pending"));
        demandRepo.save(new DemandRecord("DMD-R", "STU-2", "ITM-EXCLUDE", 25, d3, DemandStatus.REJECTED, "Rejected"));

        // Since no ISSUED demands and no HistoricalDemand exist for M3, M3 lacks coverage!
        Prediction prediction = service.predictDemandForItem("ITM-EXCLUDE", target);
        assertEquals(PredictionStatus.INSUFFICIENT_HISTORY, prediction.getStatus(),
                "Pending and rejected demands must NOT provide coverage for consumption");
    }

    public void testIssuedDemandsAreIncludedInPrediction() {
        setUp();
        Inventory item = new Inventory("ITM-ISSUED", "Test Item", "General", "", 10, 100.0, 5, LocalDate.now());
        inventoryRepo.save(item);

        YearMonth target = YearMonth.of(2026, 12);
        YearMonth m1 = target.minusMonths(3);
        YearMonth m2 = target.minusMonths(2);
        YearMonth m3 = target.minusMonths(1);

        // M1: Historical record of 10
        service.recordHistoricalDemand("ITM-ISSUED", m1.getYear(), m1.getMonthValue(), 10, "M1");
        // M2: Historical record of 10
        service.recordHistoricalDemand("ITM-ISSUED", m2.getYear(), m2.getMonthValue(), 10, "M2");
        // M3: ISSUED demand records totaling 10 (two demands of 5 each)
        LocalDate d3 = m3.atDay(15);
        demandRepo.save(new DemandRecord("DMD-I1", "STU-1", "ITM-ISSUED", 5, d3, DemandStatus.ISSUED, "Issued 1"));
        demandRepo.save(new DemandRecord("DMD-I2", "STU-2", "ITM-ISSUED", 5, d3, DemandStatus.ISSUED, "Issued 2"));

        Prediction prediction = service.predictDemandForItem("ITM-ISSUED", target);

        assertEquals(PredictionStatus.SUFFICIENT, prediction.getStatus(), "Issued demands provide valid consumption coverage");
        // Average: (10 + 10 + 10) / 3 = 10.0
        assertEquals(10.0, prediction.getPredictedDemand(), 0.0001, "Expected average = 10.0");
    }

    public void testDefaultTargetIsNextMonth() {
        setUp();
        Inventory item = new Inventory("ITM-DEF", "Test Item", "General", "", 10, 100.0, 5, LocalDate.now());
        inventoryRepo.save(item);

        YearMonth expectedNext = YearMonth.now().plusMonths(1);
        Prediction prediction = service.predictDemandForItem("ITM-DEF", null);

        assertEquals(expectedNext, prediction.getTargetYearMonth(), "Default target must be next calendar month");
    }
}
