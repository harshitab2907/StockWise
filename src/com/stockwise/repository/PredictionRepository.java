package com.stockwise.repository;

import com.stockwise.model.Prediction;
import java.util.List;
import java.util.Optional;

/**
 * Contract for generated prediction persistence.
 * Assigned to: Janvi / Harshita
 */
public interface PredictionRepository {
    Prediction save(Prediction prediction);
    Optional<Prediction> findById(String predictionId);
    List<Prediction> findAll();
    List<Prediction> findByItemId(String itemId);
    Optional<Prediction> findLatestByItemId(String itemId);
    int count();
}
