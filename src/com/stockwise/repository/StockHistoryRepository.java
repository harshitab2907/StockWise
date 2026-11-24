package com.stockwise.repository;

import com.stockwise.model.StockHistory;
import java.util.List;

/**
 * Contract for stock audit trail persistence.
 * Assigned to: Janvi / Anmol
 */
public interface StockHistoryRepository {
    StockHistory save(StockHistory history);
    List<StockHistory> findAll();
    List<StockHistory> findByItemId(String itemId);
    List<StockHistory> findRecent(int limit);
    int count();
}
