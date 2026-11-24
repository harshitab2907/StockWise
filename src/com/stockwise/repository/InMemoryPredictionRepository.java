package com.stockwise.repository;

import com.stockwise.model.Prediction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of PredictionRepository.
 * Assigned to: Janvi / Harshita
 */
public class InMemoryPredictionRepository implements PredictionRepository {
    private final Map<String, Prediction> predictionStore = new LinkedHashMap<>();

    @Override
    public synchronized Prediction save(Prediction prediction) {
        if (prediction == null) {
            throw new IllegalArgumentException("Cannot save null prediction.");
        }
        predictionStore.put(prediction.getPredictionId(), prediction);
        return prediction;
    }

    @Override
    public synchronized Optional<Prediction> findById(String predictionId) {
        if (predictionId == null) return Optional.empty();
        return Optional.ofNullable(predictionStore.get(predictionId));
    }

    @Override
    public synchronized List<Prediction> findAll() {
        List<Prediction> copy = new ArrayList<>(predictionStore.values());
        Collections.reverse(copy); // latest first
        return copy;
    }

    @Override
    public synchronized List<Prediction> findByItemId(String itemId) {
        if (itemId == null) return Collections.emptyList();
        String target = itemId.trim().toLowerCase();
        List<Prediction> filtered = predictionStore.values().stream()
                .filter(p -> p.getItemId().equalsIgnoreCase(target))
                .collect(Collectors.toList());
        Collections.reverse(filtered);
        return filtered;
    }

    @Override
    public synchronized Optional<Prediction> findLatestByItemId(String itemId) {
        if (itemId == null) return Optional.empty();
        String target = itemId.trim().toLowerCase();
        List<Prediction> list = new ArrayList<>(predictionStore.values());
        Collections.reverse(list);
        return list.stream()
                .filter(p -> p.getItemId().equalsIgnoreCase(target))
                .findFirst();
    }

    @Override
    public synchronized int count() {
        return predictionStore.size();
    }
}
