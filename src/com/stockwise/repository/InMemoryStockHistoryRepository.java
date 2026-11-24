package com.stockwise.repository;

import com.stockwise.model.StockHistory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of StockHistoryRepository.
 * Assigned to: Janvi / Anmol
 */
public class InMemoryStockHistoryRepository implements StockHistoryRepository {
    private final List<StockHistory> historyList = new ArrayList<>();

    @Override
    public synchronized StockHistory save(StockHistory history) {
        if (history == null) {
            throw new IllegalArgumentException("Cannot save null stock history.");
        }
        historyList.add(history);
        return history;
    }

    @Override
    public synchronized List<StockHistory> findAll() {
        // Return latest transactions first
        List<StockHistory> copy = new ArrayList<>(historyList);
        Collections.reverse(copy);
        return copy;
    }

    @Override
    public synchronized List<StockHistory> findByItemId(String itemId) {
        if (itemId == null) return Collections.emptyList();
        String target = itemId.trim().toLowerCase();
        List<StockHistory> filtered = historyList.stream()
                .filter(h -> h.getItemId().equalsIgnoreCase(target))
                .collect(Collectors.toList());
        Collections.reverse(filtered);
        return filtered;
    }

    @Override
    public synchronized List<StockHistory> findRecent(int limit) {
        List<StockHistory> allReversed = findAll();
        if (limit <= 0 || allReversed.isEmpty()) {
            return Collections.emptyList();
        }
        return allReversed.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public synchronized int count() {
        return historyList.size();
    }
}
