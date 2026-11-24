package com.stockwise;

import com.stockwise.exception.InsufficientStockException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import com.stockwise.model.Inventory;
import com.stockwise.model.Student;
import com.stockwise.model.TransactionType;
import com.stockwise.repository.InMemoryDemandRepository;
import com.stockwise.repository.InMemoryInventoryRepository;
import com.stockwise.repository.InMemoryStockHistoryRepository;
import com.stockwise.repository.InMemoryStudentRepository;
import com.stockwise.service.DemandService;

import java.time.LocalDate;

import static com.stockwise.TestAssertions.*;

/**
 * Unit tests verifying Module C: Demand Management.
 */
public class DemandServiceTest {

    private InMemoryDemandRepository demandRepo;
    private InMemoryInventoryRepository inventoryRepo;
    private InMemoryStudentRepository studentRepo;
    private InMemoryStockHistoryRepository stockHistoryRepo;
    private DemandService service;

    public void setUp() {
        demandRepo = new InMemoryDemandRepository();
        inventoryRepo = new InMemoryInventoryRepository();
        studentRepo = new InMemoryStudentRepository();
        stockHistoryRepo = new InMemoryStockHistoryRepository();
        service = new DemandService(demandRepo, inventoryRepo, studentRepo, stockHistoryRepo);

        // Seed reference student and item
        studentRepo.save(new Student("STU-100", "Demo Student", "ROLL-100", "CS", "stu@college.edu", "9876543210"));
        inventoryRepo.save(new Inventory("ITM-100", "Demo Item", "Electronics", "", 20, 100.0, 5, LocalDate.now()));
    }

    public void testSubmitDemandSuccessfully() {
        setUp();
        DemandRecord demand = service.submitDemand("STU-100", "ITM-100", 5, "Project requirement");
        assertNotNull(demand, "Submitted demand should not be null");
        assertEquals(DemandStatus.PENDING, demand.getStatus(), "Status must be PENDING");
        assertEquals(5, demand.getRequestedQuantity(), "Requested quantity matches");

        // Verify stock was NOT altered on submit
        Inventory item = inventoryRepo.findById("ITM-100").orElseThrow();
        assertEquals(20, item.getAvailableQuantity(), "Pending demand must NOT deduct stock");
    }

    public void testIssueDemandDeductsStockExactlyOnce() {
        setUp();
        DemandRecord demand = service.submitDemand("STU-100", "ITM-100", 6, "Lab requirement");
        assertEquals(20, inventoryRepo.findById("ITM-100").orElseThrow().getAvailableQuantity(), "Stock is 20 before issue");

        DemandRecord issued = service.issueDemand(demand.getDemandId(), "Approved by admin");
        assertEquals(DemandStatus.ISSUED, issued.getStatus(), "Status should be ISSUED");

        Inventory item = inventoryRepo.findById("ITM-100").orElseThrow();
        assertEquals(14, item.getAvailableQuantity(), "Stock must be reduced by 6 (20 - 6 = 14)");

        // Verify audit history was recorded
        assertEquals(1, stockHistoryRepo.count(), "Stock history must have 1 transaction");
        assertEquals(TransactionType.STOCK_ISSUED, stockHistoryRepo.findAll().get(0).getTransactionType(), "Transaction type is STOCK_ISSUED");
        assertEquals(-6, stockHistoryRepo.findAll().get(0).getQuantityChanged(), "Quantity changed is -6");
    }

    public void testPreventDuplicateIssuance() {
        setUp();
        DemandRecord demand = service.submitDemand("STU-100", "ITM-100", 4, "Project");
        service.issueDemand(demand.getDemandId(), "First issue");

        Inventory item = inventoryRepo.findById("ITM-100").orElseThrow();
        assertEquals(16, item.getAvailableQuantity(), "Stock is 16 after first issue");

        // Attempting to issue again must fail
        assertThrows(ValidationException.class, () -> {
            service.issueDemand(demand.getDemandId(), "Duplicate issue attempt");
        }, "Issuing an already ISSUED demand must throw ValidationException");

        // Stock must remain unchanged at 16
        assertEquals(16, inventoryRepo.findById("ITM-100").orElseThrow().getAvailableQuantity(), "Stock must not be deducted a second time");
    }

    public void testPreventInsufficientStockIssuance() {
        setUp();
        // Item has 20 in stock, student requests 25
        DemandRecord demand = service.submitDemand("STU-100", "ITM-100", 25, "Excess request");

        assertThrows(InsufficientStockException.class, () -> {
            service.issueDemand(demand.getDemandId(), "Should fail");
        }, "Issuing when requested quantity > available stock must throw InsufficientStockException");

        // Stock and status must not be modified
        Inventory item = inventoryRepo.findById("ITM-100").orElseThrow();
        assertEquals(20, item.getAvailableQuantity(), "Stock must remain 20 after failed issue");
        assertEquals(DemandStatus.PENDING, demandRepo.findById(demand.getDemandId()).orElseThrow().getStatus(), "Status remains PENDING");
    }

    public void testRejectDemandDoesNotAlterStock() {
        setUp();
        DemandRecord demand = service.submitDemand("STU-100", "ITM-100", 10, "Request");
        assertEquals(20, inventoryRepo.findById("ITM-100").orElseThrow().getAvailableQuantity(), "Stock is 20 before rejection");

        DemandRecord rejected = service.rejectDemand(demand.getDemandId(), "Out of lab policy");
        assertEquals(DemandStatus.REJECTED, rejected.getStatus(), "Status is REJECTED");

        Inventory item = inventoryRepo.findById("ITM-100").orElseThrow();
        assertEquals(20, item.getAvailableQuantity(), "Stock must remain 20 after rejection");
        assertEquals(0, stockHistoryRepo.count(), "No stock history for rejected demand");
    }
}
