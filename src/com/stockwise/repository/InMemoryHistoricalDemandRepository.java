package com.stockwise.repository;

import com.stockwise.model.HistoricalDemand;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of HistoricalDemandRepository.
 * Keyed by compound key (itemId + ":" + yearMonth).
 * Assigned to: Janvi / Harshita
 */
public class InMemoryHistoricalDemandRepository implements HistoricalDemandRepository {
    private final Map<String, HistoricalDemand> demandStore = new LinkedHashMap<>();

    private String buildKey(String itemId, YearMonth ym) {
        return itemId.trim().toUpperCase() + "::" + ym.toString();
    }

    @Override
    public synchronized HistoricalDemand save(HistoricalDemand demand) {
        if (demand == null) {
            throw new IllegalArgumentException("Cannot save null historical demand.");
        }
        String key = buildKey(demand.getItemId(), demand.getYearMonth());
        demandStore.put(key, demand);
        return demand;
    }

    @Override
    public synchronized Optional<HistoricalDemand> findByItemIdAndYearMonth(String itemId, YearMonth yearMonth) {
        if (itemId == null || yearMonth == null) return Optional.empty();
        String key = buildKey(itemId, yearMonth);
        return Optional.ofNullable(demandStore.get(key));
    }

    @Override
    public synchronized List<HistoricalDemand> findByItemId(String itemId) {
        if (itemId == null) return Collections.emptyList();
        String target = itemId.trim().toUpperCase();
        return demandStore.values().stream()
                .filter(d -> d.getItemId().equalsIgnoreCase(target))
                .sorted((a, b) -> a.getYearMonth().compareTo(b.getYearMonth()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<HistoricalDemand> findAll() {
        return new ArrayList<>(demandStore.values());
    }

    @Override
    public synchronized boolean existsByItemIdAndYearMonth(String itemId, YearMonth yearMonth) {
        if (itemId == null || yearMonth == null) return false;
        return demandStore.containsKey(buildKey(itemId, yearMonth));
    }

    @Override
    public synchronized int count() {
        return demandStore.size();
    }
}
