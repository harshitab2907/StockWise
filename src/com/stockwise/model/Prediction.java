package com.stockwise.model;

import com.stockwise.exception.ValidationException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the results of a 3-month moving average demand prediction.
 * Assigned to: Harshita (Demand Prediction)
 */
public class Prediction {

    /**
     * Helper record holding demand consumption for a single reference month.
     */
    public static class MonthConsumption {
        private final YearMonth yearMonth;
        private final int consumedQuantity;
        private final boolean covered;
        private final String sourceDescription;

        public MonthConsumption(YearMonth yearMonth, int consumedQuantity, boolean covered, String sourceDescription) {
            this.yearMonth = yearMonth;
            this.consumedQuantity = consumedQuantity;
            this.covered = covered;
            this.sourceDescription = sourceDescription;
        }

        public YearMonth getYearMonth() {
            return yearMonth;
        }

        public int getConsumedQuantity() {
            return consumedQuantity;
        }

        public boolean isCovered() {
            return covered;
        }

        public String getSourceDescription() {
            return sourceDescription;
        }

        @Override
        public String toString() {
            return yearMonth + ": " + (covered ? (consumedQuantity + " units (" + sourceDescription + ")") : "NO DATA");
        }
    }

    private String predictionId;
    private String itemId;
    private YearMonth targetYearMonth;
    private double predictedDemand;
    private Integer actualDemand; // nullable, available when target month concludes
    private int suggestedRestockQuantity;
    private LocalDate predictionDate;
    private PredictionStatus status;
    private boolean highDemand;
    private List<MonthConsumption> referenceMonths;
    private String explanation;

    public Prediction(String predictionId, String itemId, YearMonth targetYearMonth,
                      double predictedDemand, int suggestedRestockQuantity,
                      PredictionStatus status, boolean highDemand,
                      List<MonthConsumption> referenceMonths, String explanation) {
        validatePredictionId(predictionId);
        validateItemId(itemId);
        if (targetYearMonth == null) {
            throw new ValidationException("Target YearMonth cannot be null.");
        }
        if (status == null) {
            throw new ValidationException("Prediction status cannot be null.");
        }

        this.predictionId = predictionId.trim();
        this.itemId = itemId.trim();
        this.targetYearMonth = targetYearMonth;
        this.predictedDemand = predictedDemand;
        this.actualDemand = null;
        this.suggestedRestockQuantity = suggestedRestockQuantity;
        this.predictionDate = LocalDate.now();
        this.status = status;
        this.highDemand = highDemand;
        this.referenceMonths = (referenceMonths != null) ? new ArrayList<>(referenceMonths) : new ArrayList<>();
        this.explanation = (explanation == null) ? "" : explanation.trim();
    }

    public static void validatePredictionId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Prediction ID cannot be empty.");
        }
    }

    public static void validateItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID cannot be empty.");
        }
    }

    // Getters and Setters

    public String getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(String predictionId) {
        validatePredictionId(predictionId);
        this.predictionId = predictionId.trim();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        validateItemId(itemId);
        this.itemId = itemId.trim();
    }

    public YearMonth getTargetYearMonth() {
        return targetYearMonth;
    }

    public void setTargetYearMonth(YearMonth targetYearMonth) {
        if (targetYearMonth == null) {
            throw new ValidationException("Target YearMonth cannot be null.");
        }
        this.targetYearMonth = targetYearMonth;
    }

    public int getTargetMonth() {
        return targetYearMonth.getMonthValue();
    }

    public int getTargetYear() {
        return targetYearMonth.getYear();
    }

    public double getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(double predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public Integer getActualDemand() {
        return actualDemand;
    }

    public void setActualDemand(Integer actualDemand) {
        this.actualDemand = actualDemand;
    }

    public int getSuggestedRestockQuantity() {
        return suggestedRestockQuantity;
    }

    public void setSuggestedRestockQuantity(int suggestedRestockQuantity) {
        this.suggestedRestockQuantity = suggestedRestockQuantity;
    }

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDate) {
        this.predictionDate = (predictionDate != null) ? predictionDate : LocalDate.now();
    }

    public PredictionStatus getStatus() {
        return status;
    }

    public void setStatus(PredictionStatus status) {
        if (status == null) {
            throw new ValidationException("Prediction status cannot be null.");
        }
        this.status = status;
    }

    public boolean isHighDemand() {
        return highDemand;
    }

    public void setHighDemand(boolean highDemand) {
        this.highDemand = highDemand;
    }

    public List<MonthConsumption> getReferenceMonths() {
        return Collections.unmodifiableList(referenceMonths);
    }

    public void setReferenceMonths(List<MonthConsumption> referenceMonths) {
        this.referenceMonths = (referenceMonths != null) ? new ArrayList<>(referenceMonths) : new ArrayList<>();
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = (explanation == null) ? "" : explanation.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prediction that = (Prediction) o;
        return Objects.equals(predictionId, that.predictionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictionId);
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "predictionId='" + predictionId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", targetYearMonth=" + targetYearMonth +
                ", predictedDemand=" + String.format("%.2f", predictedDemand) +
                ", suggestedRestock=" + suggestedRestockQuantity +
                ", status=" + status +
                ", isHighDemand=" + highDemand +
                '}';
    }
}
