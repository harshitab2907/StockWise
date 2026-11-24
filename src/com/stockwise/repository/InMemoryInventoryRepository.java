package com.stockwise.repository;

import com.stockwise.model.Inventory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of InventoryRepository using Map and ArrayList.
 * Assigned to: Janvi / Anmol
 */
public class InMemoryInventoryRepository implements InventoryRepository {
    private final Map<String, Inventory> itemStore = new LinkedHashMap<>();

    @Override
    public synchronized Inventory save(Inventory item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot save null inventory item.");
        }
        itemStore.put(item.getItemId(), item);
        return item;
    }

    @Override
    public synchronized Optional<Inventory> findById(String itemId) {
        if (itemId == null) return Optional.empty();
        return Optional.ofNullable(itemStore.get(itemId));
    }

    @Override
    public synchronized List<Inventory> findAll() {
        return new ArrayList<>(itemStore.values());
    }

    @Override
    public synchronized List<Inventory> findByNameContaining(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String query = name.trim().toLowerCase();
        return itemStore.values().stream()
                .filter(i -> i.getItemName().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Inventory> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String cat = category.trim().toLowerCase();
        return itemStore.values().stream()
                .filter(i -> i.getCategory().toLowerCase().equals(cat))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean deleteById(String itemId) {
        if (itemId == null) return false;
        return itemStore.remove(itemId) != null;
    }

    @Override
    public synchronized boolean existsById(String itemId) {
        if (itemId == null) return false;
        return itemStore.containsKey(itemId);
    }

    @Override
    public synchronized int count() {
        return itemStore.size();
    }
}
