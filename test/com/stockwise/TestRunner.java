package com.stockwise;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard Java test runner executing all module test suites without external dependencies.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("           STOCKWISE AUTOMATED TEST SUITE           ");
        System.out.println("   Evaluation 1: Core Java 17 In-Memory Verification");
        System.out.println("====================================================");

        List<Class<?>> testClasses = List.of(
                InventoryServiceTest.class,
                StudentServiceTest.class,
                DemandServiceTest.class,
                StockHistoryTest.class,
                PredictionServiceTest.class
        );

        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        List<String> failureDetails = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (Class<?> clazz : testClasses) {
            System.out.println("\nRunning Suite: " + clazz.getSimpleName());
            System.out.println("----------------------------------------------------");

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("test") && Modifier.isPublic(method.getModifiers())) {
                    totalTests++;
                    try {
                        Object testInstance = clazz.getDeclaredConstructor().newInstance();
                        method.invoke(testInstance);
                        passedTests++;
                        System.out.printf("  [PASS] %-48s%n", method.getName());
                    } catch (Throwable t) {
                        failedTests++;
                        Throwable cause = t.getCause() != null ? t.getCause() : t;
                        String failMsg = String.format("%s.%s: %s", clazz.getSimpleName(), method.getName(), cause.getMessage());
                        failureDetails.add(failMsg);
                        System.out.printf("  [FAIL] %-48s%n", method.getName());
                        System.out.println("         -> " + cause.getMessage());
                    }
                }
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;

        System.out.println("\n====================================================");
        System.out.println("                   TEST RESULTS                     ");
        System.out.println("====================================================");
        System.out.printf("  Total Tests Executed: %d%n", totalTests);
        System.out.printf("  Passed:               %d%n", passedTests);
        System.out.printf("  Failed:               %d%n", failedTests);
        System.out.printf("  Execution Time:       %d ms%n", elapsed);
        System.out.println("====================================================");

        if (failedTests > 0) {
            System.out.println("\nFAILURE SUMMARY:");
            for (String failure : failureDetails) {
                System.out.println("  * " + failure);
            }
            System.exit(1);
        } else {
            System.out.println("\nALL TESTS PASSED SUCCESSFULLY! (100% SUCCESS RATE)");
            System.exit(0);
        }
    }
}
