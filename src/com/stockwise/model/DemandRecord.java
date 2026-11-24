package com.stockwise.model;

import com.stockwise.exception.ValidationException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a student request for an inventory item.
 * Assigned to: Sedhat (Demand Management)
 */
public class DemandRecord {
    private String demandId;
    private String studentId;
    private String itemId;
    private int requestedQuantity;
    private LocalDate requestDate;
    private DemandStatus status;
    private String remarks;

    public DemandRecord(String demandId, String studentId, String itemId,
                        int requestedQuantity, LocalDate requestDate, DemandStatus status, String remarks) {
        validateDemandId(demandId);
        validateStudentId(studentId);
        validateItemId(itemId);
        validateRequestedQuantity(requestedQuantity);

        this.demandId = demandId.trim();
        this.studentId = studentId.trim();
        this.itemId = itemId.trim();
        this.requestedQuantity = requestedQuantity;
        this.requestDate = (requestDate != null) ? requestDate : LocalDate.now();
        this.status = (status != null) ? status : DemandStatus.PENDING;
        this.remarks = (remarks == null) ? "" : remarks.trim();
    }

    public DemandRecord(String demandId, String studentId, String itemId, int requestedQuantity) {
        this(demandId, studentId, itemId, requestedQuantity, LocalDate.now(), DemandStatus.PENDING, "New demand request");
    }

    public static void validateDemandId(String demandId) {
        if (demandId == null || demandId.trim().isEmpty()) {
            throw new ValidationException("Demand ID cannot be empty.");
        }
    }

    public static void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty.");
        }
    }

    public static void validateItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID cannot be empty.");
        }
    }

    public static void validateRequestedQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Requested quantity must be greater than zero: " + quantity);
        }
    }

    // Getters and Setters

    public String getDemandId() {
        return demandId;
    }

    public void setDemandId(String demandId) {
        validateDemandId(demandId);
        this.demandId = demandId.trim();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        validateStudentId(studentId);
        this.studentId = studentId.trim();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        validateItemId(itemId);
        this.itemId = itemId.trim();
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        validateRequestedQuantity(requestedQuantity);
        this.requestedQuantity = requestedQuantity;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = (requestDate != null) ? requestDate : LocalDate.now();
    }

    public DemandStatus getStatus() {
        return status;
    }

    public void setStatus(DemandStatus status) {
        if (status == null) {
            throw new ValidationException("Demand status cannot be null.");
        }
        this.status = status;
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
        DemandRecord that = (DemandRecord) o;
        return Objects.equals(demandId, that.demandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(demandId);
    }

    @Override
    public String toString() {
        return "DemandRecord{" +
                "demandId='" + demandId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", requestedQuantity=" + requestedQuantity +
                ", requestDate=" + requestDate +
                ", status=" + status +
                '}';
    }
}
