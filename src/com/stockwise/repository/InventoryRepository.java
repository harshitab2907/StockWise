package com.stockwise.repository;

import com.stockwise.model.Inventory;
import java.util.List;
import java.util.Optional;

/**
 * Contract for inventory persistence operations.
 * Allows seamless swap with JDBC/MySQL in Evaluation 2.
 * Assigned to: Janvi / Anmol
 */
public interface InventoryRepository {
    Inventory save(Inventory item);
    Optional<Inventory> findById(String itemId);
    List<Inventory> findAll();
    List<Inventory> findByNameContaining(String name);
    List<Inventory> findByCategory(String category);
    boolean deleteById(String itemId);
    boolean existsById(String itemId);
    int count();
}
