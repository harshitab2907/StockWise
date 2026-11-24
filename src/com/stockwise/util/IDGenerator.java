package com.stockwise.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe ID Generator providing standardized, formatted business keys.
 * Assigned to: Janvi (Shared Data Architecture)
 */
public class IDGenerator {

    private static final AtomicInteger ITEM_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger STUDENT_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger DEMAND_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger HISTORY_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger HISTORICAL_DEMAND_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger PREDICTION_SEQ = new AtomicInteger(1000);

    private IDGenerator() {
        // Utility class
    }

    public static String generateItemId() {
        return "ITM-" + ITEM_SEQ.incrementAndGet();
    }

    public static String generateStudentId() {
        return "STU-" + STUDENT_SEQ.incrementAndGet();
    }

    public static String generateDemandId() {
        return "DMD-" + DEMAND_SEQ.incrementAndGet();
    }

    public static String generateHistoryId() {
        return "TXN-" + HISTORY_SEQ.incrementAndGet();
    }

    public static String generateHistoricalDemandId() {
        return "HD-" + HISTORICAL_DEMAND_SEQ.incrementAndGet();
    }

    public static String generatePredictionId() {
        return "PRD-" + PREDICTION_SEQ.incrementAndGet();
    }

    /**
     * Resets counters to base for automated unit test isolation.
     */
    public static void resetSequences() {
        ITEM_SEQ.set(1000);
        STUDENT_SEQ.set(1000);
        DEMAND_SEQ.set(1000);
        HISTORY_SEQ.set(1000);
        HISTORICAL_DEMAND_SEQ.set(1000);
        PREDICTION_SEQ.set(1000);
    }
}
