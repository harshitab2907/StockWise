package com.stockwise;

import com.stockwise.storage.AppContext;
import com.stockwise.ui.ConsoleInput;
import com.stockwise.ui.ConsoleMenu;

/**
 * Main application entry point for StockWise — Student Inventory Demand Predictor.
 * Tagline: Smart Inventory. Smarter Decisions.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Initializing StockWise System...");

        // 1. Initialize shared application context (repositories & services)
        AppContext context = new AppContext();

        // 2. Pre-seed rich initial dataset for academic viva demonstration
        context.seedInitialData();

        // 3. Initialize console input and menu
        ConsoleInput input = new ConsoleInput();
        ConsoleMenu menu = new ConsoleMenu(context, input);

        // 4. Launch interactive console application
        menu.start();
    }
}
