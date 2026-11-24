package com.stockwise.repository;

import com.stockwise.model.HistoricalDemand;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Contract for historical monthly consumption data persistence.
 * Assigned to: Janvi / Harshita
 */
public interface HistoricalDemandRepository {
    HistoricalDemand save(HistoricalDemand demand);
    Optional<HistoricalDemand> findByItemIdAndYearMonth(String itemId, YearMonth yearMonth);
    List<HistoricalDemand> findByItemId(String itemId);
    List<HistoricalDemand> findAll();
    boolean existsByItemIdAndYearMonth(String itemId, YearMonth yearMonth);
    int count();
}
