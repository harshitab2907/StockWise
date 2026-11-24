package com.stockwise.repository;

import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import java.util.List;
import java.util.Optional;

/**
 * Contract for student demand request persistence.
 * Assigned to: Janvi / Sedhat
 */
public interface DemandRepository {
    DemandRecord save(DemandRecord demand);
    Optional<DemandRecord> findById(String demandId);
    List<DemandRecord> findAll();
    List<DemandRecord> findByStatus(DemandStatus status);
    List<DemandRecord> findByItemId(String itemId);
    List<DemandRecord> findByStudentId(String studentId);
    List<DemandRecord> findByItemIdAndStatus(String itemId, DemandStatus status);
    boolean deleteById(String demandId);
    int count();
    int countByStatus(DemandStatus status);
}
