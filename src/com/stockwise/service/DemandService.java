package com.stockwise.service;

import com.stockwise.exception.InsufficientStockException;
import com.stockwise.exception.ResourceNotFoundException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import com.stockwise.model.Inventory;
import com.stockwise.model.StockHistory;
import com.stockwise.model.Student;
import com.stockwise.model.TransactionType;
import com.stockwise.repository.DemandRepository;
import com.stockwise.repository.InventoryRepository;
import com.stockwise.repository.StockHistoryRepository;
import com.stockwise.repository.StudentRepository;
import com.stockwise.util.IDGenerator;
import com.stockwise.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Service orchestrating student demand requests, approvals, issues, and rejections.
 * Enforces atomic stock deductions and audit trail consistency.
 * Assigned to: Sidak (Demand Management)
 */
public class DemandService {

    private final DemandRepository demandRepository;
    private final InventoryRepository inventoryRepository;
    private final StudentRepository studentRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public DemandService(DemandRepository demandRepository,
                         InventoryRepository inventoryRepository,
                         StudentRepository studentRepository,
                         StockHistoryRepository stockHistoryRepository) {
        this.demandRepository = demandRepository;
        this.inventoryRepository = inventoryRepository;
        this.studentRepository = studentRepository;
        this.stockHistoryRepository = stockHistoryRepository;
    }

    /**
     * Submits a new demand request. Status is PENDING. Does not modify active stock.
     */
    public synchronized DemandRecord submitDemand(String studentId, String itemId, int requestedQuantity, String remarks) {
        ValidationUtils.requireNonEmpty(studentId, "Student ID");
        ValidationUtils.requireNonEmpty(itemId, "Item ID");
        ValidationUtils.requirePositive(requestedQuantity, "Requested quantity");

        // Validate student exists
        Student student = studentRepository.findById(studentId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID '" + studentId + "' not found."));

        // Validate item exists
        Inventory item = inventoryRepository.findById(itemId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with ID '" + itemId + "' not found."));

        String demandId = IDGenerator.generateDemandId();
        DemandRecord demand = new DemandRecord(
                demandId,
                student.getStudentId(),
                item.getItemId(),
                requestedQuantity,
                LocalDate.now(),
                DemandStatus.PENDING,
                (remarks == null || remarks.trim().isEmpty()) ? "Submitted by student" : remarks.trim()
        );

        return demandRepository.save(demand);
    }

    /**
     * Approves and issues a pending demand request.
     * Enforces the critical business rule:
     * 1. Demand exists.
     * 2. Status is PENDING.
     * 3. Available stock >= requestedQuantity.
     * 4. Stock deducted exactly once.
     * 5. Status updated to ISSUED.
     * 6. StockHistory recorded.
     */
    public synchronized DemandRecord issueDemand(String demandId, String adminRemarks) {
        ValidationUtils.requireNonEmpty(demandId, "Demand ID");

        // 1. Confirm demand exists
        DemandRecord demand = demandRepository.findById(demandId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Demand record with ID '" + demandId + "' not found."));

        // 2. Confirm current status is PENDING
        if (demand.getStatus() != DemandStatus.PENDING) {
            throw new ValidationException("Demand '" + demandId + "' is already " + demand.getStatus() +
                    ". Only PENDING demands can be issued.");
        }

        // 3. Confirm inventory item exists
        Inventory item = inventoryRepository.findById(demand.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with ID '" + demand.getItemId() + "' not found."));

        // 4. Ensure requested quantity does not exceed available stock
        int requestedQty = demand.getRequestedQuantity();
        int availableQty = item.getAvailableQuantity();
        if (availableQty < requestedQty) {
            throw new InsufficientStockException("Cannot issue demand " + demandId + ": Requested quantity (" +
                    requestedQty + ") exceeds available stock (" + availableQty + ") for item '" + item.getItemName() + "'.");
        }

        // 5. Deduct stock exactly once
        item.setAvailableQuantity(availableQty - requestedQty);
        inventoryRepository.save(item);

        // 6. Update demand status to ISSUED
        demand.setStatus(DemandStatus.ISSUED);
        String updatedRemarks = (adminRemarks == null || adminRemarks.trim().isEmpty())
                ? "Approved and issued on " + LocalDate.now()
                : adminRemarks.trim();
        demand.setRemarks(updatedRemarks);
        demandRepository.save(demand);

        // 7. Add stock-history record
        StockHistory history = new StockHistory(
                IDGenerator.generateHistoryId(),
                item.getItemId(),
                TransactionType.STOCK_ISSUED,
                -requestedQty,
                LocalDate.now(),
                "Stock issued to student: " + demand.getStudentId() + " (Demand: " + demand.getDemandId() + ")"
        );
        stockHistoryRepository.save(history);

        return demand;
    }

    /**
     * Rejects a pending demand request. Does not touch inventory stock.
     */
    public synchronized DemandRecord rejectDemand(String demandId, String rejectionReason) {
        ValidationUtils.requireNonEmpty(demandId, "Demand ID");

        DemandRecord demand = demandRepository.findById(demandId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Demand record with ID '" + demandId + "' not found."));

        if (demand.getStatus() != DemandStatus.PENDING) {
            throw new ValidationException("Demand '" + demandId + "' is already " + demand.getStatus() +
                    ". Only PENDING demands can be rejected.");
        }

        demand.setStatus(DemandStatus.REJECTED);
        String reason = (rejectionReason == null || rejectionReason.trim().isEmpty())
                ? "Rejected by administrator"
                : "Rejected: " + rejectionReason.trim();
        demand.setRemarks(reason);

        return demandRepository.save(demand);
    }

    public List<DemandRecord> getAllDemands() {
        return demandRepository.findAll();
    }

    public DemandRecord getDemandById(String demandId) {
        ValidationUtils.requireNonEmpty(demandId, "Demand ID");
        return demandRepository.findById(demandId)
                .orElseThrow(() -> new ResourceNotFoundException("Demand with ID '" + demandId + "' not found."));
    }

    public List<DemandRecord> getPendingDemands() {
        return demandRepository.findByStatus(DemandStatus.PENDING);
    }

    public List<DemandRecord> getIssuedDemands() {
        return demandRepository.findByStatus(DemandStatus.ISSUED);
    }

    public List<DemandRecord> getRejectedDemands() {
        return demandRepository.findByStatus(DemandStatus.REJECTED);
    }

    public List<DemandRecord> getDemandsByStudent(String studentId) {
        return demandRepository.findByStudentId(studentId);
    }

    public List<DemandRecord> getDemandsByItem(String itemId) {
        return demandRepository.findByItemId(itemId);
    }

    public int getTotalDemandCount() {
        return demandRepository.count();
    }

    public int getPendingCount() {
        return demandRepository.countByStatus(DemandStatus.PENDING);
    }

    public int getIssuedCount() {
        return demandRepository.countByStatus(DemandStatus.ISSUED);
    }

    public int getRejectedCount() {
        return demandRepository.countByStatus(DemandStatus.REJECTED);
    }
}
