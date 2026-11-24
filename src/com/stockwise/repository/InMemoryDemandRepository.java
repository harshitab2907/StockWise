package com.stockwise.repository;

import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of DemandRepository.
 * Assigned to: Janvi / Sidak
 */
public class InMemoryDemandRepository implements DemandRepository {
    private final Map<String, DemandRecord> demandStore = new LinkedHashMap<>();

    @Override
    public synchronized DemandRecord save(DemandRecord demand) {
        if (demand == null) {
            throw new IllegalArgumentException("Cannot save null demand record.");
        }
        demandStore.put(demand.getDemandId(), demand);
        return demand;
    }

    @Override
    public synchronized Optional<DemandRecord> findById(String demandId) {
        if (demandId == null) return Optional.empty();
        return Optional.ofNullable(demandStore.get(demandId));
    }

    @Override
    public synchronized List<DemandRecord> findAll() {
        return new ArrayList<>(demandStore.values());
    }

    @Override
    public synchronized List<DemandRecord> findByStatus(DemandStatus status) {
        if (status == null) return Collections.emptyList();
        return demandStore.values().stream()
                .filter(d -> d.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<DemandRecord> findByItemId(String itemId) {
        if (itemId == null) return Collections.emptyList();
        return demandStore.values().stream()
                .filter(d -> d.getItemId().equalsIgnoreCase(itemId.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<DemandRecord> findByStudentId(String studentId) {
        if (studentId == null) return Collections.emptyList();
        return demandStore.values().stream()
                .filter(d -> d.getStudentId().equalsIgnoreCase(studentId.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<DemandRecord> findByItemIdAndStatus(String itemId, DemandStatus status) {
        if (itemId == null || status == null) return Collections.emptyList();
        return demandStore.values().stream()
                .filter(d -> d.getItemId().equalsIgnoreCase(itemId.trim()) && d.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean deleteById(String demandId) {
        if (demandId == null) return false;
        return demandStore.remove(demandId) != null;
    }

    @Override
    public synchronized int count() {
        return demandStore.size();
    }

    @Override
    public synchronized int countByStatus(DemandStatus status) {
        if (status == null) return 0;
        return (int) demandStore.values().stream().filter(d -> d.getStatus() == status).count();
    }
}
