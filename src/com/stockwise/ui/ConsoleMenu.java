package com.stockwise.ui;

import com.stockwise.exception.StockWiseException;
import com.stockwise.model.DemandRecord;
import com.stockwise.model.DemandStatus;
import com.stockwise.model.HistoricalDemand;
import com.stockwise.model.Inventory;
import com.stockwise.model.Prediction;
import com.stockwise.model.PredictionStatus;
import com.stockwise.model.StockHistory;
import com.stockwise.model.StockStatus;
import com.stockwise.model.Student;
import com.stockwise.service.DemandService;
import com.stockwise.service.InventoryService;
import com.stockwise.service.PredictionService;
import com.stockwise.service.ReportService;
import com.stockwise.service.StockAlertService;
import com.stockwise.service.StockAlertService.StockAlertItem;
import com.stockwise.service.StudentService;
import com.stockwise.storage.AppContext;
import com.stockwise.util.DateUtils;

import java.time.YearMonth;
import java.util.List;

/**
 * Console user interface implementing the StockWise brand identity and modular submenus.
 */
public class ConsoleMenu {

    private final AppContext context;
    private final ConsoleInput input;

    private final InventoryService inventoryService;
    private final StudentService studentService;
    private final DemandService demandService;
    private final PredictionService predictionService;
    private final StockAlertService stockAlertService;
    private final ReportService reportService;

    public ConsoleMenu(AppContext context, ConsoleInput input) {
        this.context = context;
        this.input = input;
        this.inventoryService = context.getInventoryService();
        this.studentService = context.getStudentService();
        this.demandService = context.getDemandService();
        this.predictionService = context.getPredictionService();
        this.stockAlertService = context.getStockAlertService();
        this.reportService = context.getReportService();
    }

    public void start() {
        boolean running = true;
        while (running) {
            printBrandHeader("MAIN MENU");
            System.out.println("  1. Inventory Management");
            System.out.println("  2. Student Management");
            System.out.println("  3. Demand Management");
            System.out.println("  4. Stock History");
            System.out.println("  5. Demand Prediction");
            System.out.println("  6. Stock Alerts");
            System.out.println("  7. Dashboard and Reports");
            System.out.println("  8. Exit");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-8): ", 1, 8);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> handleInventoryMenu();
                    case 2 -> handleStudentMenu();
                    case 3 -> handleDemandMenu();
                    case 4 -> handleStockHistoryMenu();
                    case 5 -> handlePredictionMenu();
                    case 6 -> handleStockAlertsMenu();
                    case 7 -> handleReportsMenu();
                    case 8 -> {
                        System.out.println("====================================================");
                        System.out.println("   Thank you for using StockWise.");
                        System.out.println("   Smart Inventory. Smarter Decisions.");
                        System.out.println("====================================================");
                        running = false;
                    }
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            } catch (Exception e) {
                System.out.println("\n  [UNEXPECTED ERROR] An unexpected error occurred: " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void printBrandHeader(String subtitle) {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("                     STOCKWISE");
        System.out.println("          SMART INVENTORY. SMARTER DECISIONS.");
        System.out.println("====================================================");
        if (subtitle != null && !subtitle.isEmpty()) {
            System.out.printf("%" + (26 + subtitle.length() / 2) + "s%n", subtitle);
            System.out.println("----------------------------------------------------");
        }
    }

    // =========================================================================
    // MODULE A — INVENTORY MANAGEMENT
    // =========================================================================

    private void handleInventoryMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("INVENTORY MANAGEMENT");
            System.out.println("  1. Add Inventory Item");
            System.out.println("  2. View All Items");
            System.out.println("  3. Search Item by ID");
            System.out.println("  4. Search Item by Name");
            System.out.println("  5. Update Item Details");
            System.out.println("  6. Delete Item");
            System.out.println("  7. Add Stock (Replenish)");
            System.out.println("  8. Adjust Stock Level");
            System.out.println("  9. Stock Health Overview");
            System.out.println("  10. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-10): ", 1, 10);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> addInventoryItem();
                    case 2 -> viewAllInventoryItems();
                    case 3 -> searchItemById();
                    case 4 -> searchItemByName();
                    case 5 -> updateItemDetails();
                    case 6 -> deleteInventoryItem();
                    case 7 -> addStock();
                    case 8 -> adjustStockLevel();
                    case 9 -> viewStockHealthOverview();
                    case 10 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void addInventoryItem() {
        System.out.println("--- Add New Inventory Item ---");
        String itemId = input.readOptionalString("Item ID (press Enter for auto-generation)", null);
        String name = input.readString("Item Name: ");
        String category = input.readOptionalString("Category (e.g. Lab Equipment, Electronics)", "General");
        String desc = input.readOptionalString("Description", "");
        int qty = input.readInt("Initial Available Quantity: ");
        double price = input.readDouble("Unit Price ($/Rs): ");
        int minStock = input.readInt("Minimum Stock Threshold (Safety Stock): ");

        Inventory item = inventoryService.addItem(itemId, name, category, desc, qty, price, minStock);
        System.out.println("\n  [SUCCESS] Inventory item created successfully!");
        System.out.println("  ID: " + item.getItemId() + " | Name: " + item.getItemName() + " | Status: " + item.getStockStatus().getDisplayName());
        input.waitForEnter();
    }

    private void viewAllInventoryItems() {
        List<Inventory> items = inventoryService.getAllItems();
        System.out.println("--- All Inventory Items (" + items.size() + " total) ---");
        renderInventoryTable(items);
        input.waitForEnter();
    }

    private void renderInventoryTable(List<Inventory> items) {
        TableFormatter table = new TableFormatter("Item ID", "Item Name", "Category", "Price", "Available", "Min Stock", "Status");
        table.setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(4, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.RIGHT)
             .setAlignment(6, TableFormatter.Alignment.CENTER);

        for (Inventory i : items) {
            table.addRow(
                    i.getItemId(),
                    i.getItemName(),
                    i.getCategory(),
                    String.format("%.2f", i.getPrice()),
                    i.getAvailableQuantity(),
                    i.getMinimumStock(),
                    i.getStockStatus().getDisplayName()
            );
        }
        table.print();
    }

    private void searchItemById() {
        String id = input.readString("Enter Item ID to search: ");
        Inventory item = inventoryService.getItemById(id);
        displayItemDetails(item);
        input.waitForEnter();
    }

    private void searchItemByName() {
        String query = input.readString("Enter search keyword for Item Name: ");
        List<Inventory> matches = inventoryService.searchByName(query);
        System.out.println("--- Search Results (" + matches.size() + " found) ---");
        renderInventoryTable(matches);
        input.waitForEnter();
    }

    private void displayItemDetails(Inventory item) {
        System.out.println("\n+---------------------------------------------------+");
        System.out.println("| INVENTORY ITEM DETAILS                            |");
        System.out.println("+---------------------------------------------------+");
        System.out.printf("| Item ID:             %-28s |%n", item.getItemId());
        System.out.printf("| Item Name:           %-28s |%n", item.getItemName());
        System.out.printf("| Category:            %-28s |%n", item.getCategory());
        System.out.printf("| Unit Price:          %-28.2f |%n", item.getPrice());
        System.out.printf("| Available Quantity:  %-28d |%n", item.getAvailableQuantity());
        System.out.printf("| Min Stock Threshold: %-28d |%n", item.getMinimumStock());
        System.out.printf("| Stock Status:        %-28s |%n", item.getStockStatus().getDisplayName());
        System.out.printf("| Date Added:          %-28s |%n", DateUtils.formatDate(item.getDateAdded()));
        System.out.printf("| Description:         %-28s |%n", item.getDescription());
        System.out.println("+---------------------------------------------------+");
    }

    private void updateItemDetails() {
        String id = input.readString("Enter Item ID to update: ");
        Inventory item = inventoryService.getItemById(id);
        displayItemDetails(item);

        System.out.println("\nEnter new values (press Enter to keep current):");
        String newName = input.readOptionalString("New Name", item.getItemName());
        String newCategory = input.readOptionalString("New Category", item.getCategory());
        String newDesc = input.readOptionalString("New Description", item.getDescription());
        Double newPrice = input.readOptionalDouble("New Price", item.getPrice());
        Integer newMinStock = input.readOptionalInt("New Minimum Stock", item.getMinimumStock());

        inventoryService.updateItem(id, newName, newCategory, newDesc, newPrice, newMinStock);
        System.out.println("\n  [SUCCESS] Item details updated successfully.");
        input.waitForEnter();
    }

    private void deleteInventoryItem() {
        String id = input.readString("Enter Item ID to delete: ");
        Inventory item = inventoryService.getItemById(id);
        displayItemDetails(item);

        if (input.readConfirmation("Are you sure you want to permanently delete this item?")) {
            inventoryService.deleteItem(id);
            System.out.println("\n  [SUCCESS] Item '" + item.getItemName() + "' deleted successfully.");
        } else {
            System.out.println("\n  Deletion cancelled.");
        }
        input.waitForEnter();
    }

    private void addStock() {
        String id = input.readString("Enter Item ID to replenish: ");
        Inventory item = inventoryService.getItemById(id);
        System.out.println("  Current Stock: " + item.getAvailableQuantity() + " units (" + item.getItemName() + ")");

        int addQty = input.readInt("Quantity to add: ");
        String remarks = input.readOptionalString("Remarks / Invoice reference", "Stock replenishment");

        inventoryService.addStock(id, addQty, remarks);
        System.out.println("\n  [SUCCESS] Stock added successfully! New available quantity: " + item.getAvailableQuantity());
        input.waitForEnter();
    }

    private void adjustStockLevel() {
        String id = input.readString("Enter Item ID to adjust: ");
        Inventory item = inventoryService.getItemById(id);
        System.out.println("  Current Stock: " + item.getAvailableQuantity() + " units (" + item.getItemName() + ")");

        int newQty = input.readInt("Enter new corrected stock quantity: ");
        String reason = input.readString("Reason for adjustment (e.g. Physical audit, Damaged goods): ");

        inventoryService.adjustStock(id, newQty, reason);
        System.out.println("\n  [SUCCESS] Stock adjusted successfully! New available quantity: " + item.getAvailableQuantity());
        input.waitForEnter();
    }

    private void viewStockHealthOverview() {
        System.out.println("=== Stock Health Overview ===");
        System.out.println("  In-Stock Items:     " + inventoryService.getInStockItems().size());
        System.out.println("  Low-Stock Items:    " + inventoryService.getLowStockItems().size());
        System.out.println("  Out-of-Stock Items: " + inventoryService.getOutOfStockItems().size());
        System.out.println("  Total Stock Units:  " + inventoryService.getTotalAvailableUnits());
        System.out.printf("  Total Valuation:    $%.2f%n%n", inventoryService.getTotalInventoryValuation());

        System.out.println("--- Low Stock & Out of Stock Items ---");
        List<Inventory> attentionItems = inventoryService.getAllItems().stream()
                .filter(i -> i.getStockStatus() != StockStatus.IN_STOCK)
                .toList();
        renderInventoryTable(attentionItems);
        input.waitForEnter();
    }

    // =========================================================================
    // MODULE B — STUDENT MANAGEMENT
    // =========================================================================

    private void handleStudentMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("STUDENT MANAGEMENT");
            System.out.println("  1. Add Student");
            System.out.println("  2. View All Students");
            System.out.println("  3. Search Student by ID");
            System.out.println("  4. Search Student by Roll Number");
            System.out.println("  5. Search Students by Name");
            System.out.println("  6. Update Student Details");
            System.out.println("  7. Delete Student");
            System.out.println("  8. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-8): ", 1, 8);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAllStudents();
                    case 3 -> searchStudentById();
                    case 4 -> searchStudentByRollNumber();
                    case 5 -> searchStudentsByName();
                    case 6 -> updateStudentDetails();
                    case 7 -> deleteStudent();
                    case 8 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void addStudent() {
        System.out.println("--- Register New Student ---");
        String id = input.readOptionalString("Student ID (press Enter for auto-generation)", null);
        String name = input.readString("Student Full Name: ");
        String roll = input.readString("Roll Number: ");
        String dept = input.readOptionalString("Department (e.g. CS, IT, ECE)", "Computer Science");
        String email = input.readString("Email (e.g. student@college.edu): ");
        String phone = input.readString("Phone Number: ");

        Student student = studentService.addStudent(id, name, roll, dept, email, phone);
        System.out.println("\n  [SUCCESS] Student registered successfully!");
        System.out.println("  ID: " + student.getStudentId() + " | Roll No: " + student.getRollNumber() + " | Name: " + student.getStudentName());
        input.waitForEnter();
    }

    private void viewAllStudents() {
        List<Student> students = studentService.getAllStudents();
        System.out.println("--- Student Directory (" + students.size() + " total) ---");
        renderStudentTable(students);
        input.waitForEnter();
    }

    private void renderStudentTable(List<Student> students) {
        TableFormatter table = new TableFormatter("Student ID", "Roll Number", "Name", "Department", "Email", "Phone");
        for (Student s : students) {
            table.addRow(s.getStudentId(), s.getRollNumber(), s.getStudentName(), s.getDepartment(), s.getEmail(), s.getPhoneNumber());
        }
        table.print();
    }

    private void searchStudentById() {
        String id = input.readString("Enter Student ID: ");
        Student s = studentService.getStudentById(id);
        displayStudentDetails(s);
        input.waitForEnter();
    }

    private void searchStudentByRollNumber() {
        String roll = input.readString("Enter Roll Number: ");
        Student s = studentService.getStudentByRollNumber(roll);
        displayStudentDetails(s);
        input.waitForEnter();
    }

    private void searchStudentsByName() {
        String q = input.readString("Enter name keyword: ");
        List<Student> list = studentService.searchByName(q);
        System.out.println("--- Search Results (" + list.size() + " found) ---");
        renderStudentTable(list);
        input.waitForEnter();
    }

    private void displayStudentDetails(Student s) {
        System.out.println("\n+---------------------------------------------------+");
        System.out.println("| STUDENT PROFILE                                   |");
        System.out.println("+---------------------------------------------------+");
        System.out.printf("| Student ID:   %-35s |%n", s.getStudentId());
        System.out.printf("| Roll Number:  %-35s |%n", s.getRollNumber());
        System.out.printf("| Full Name:    %-35s |%n", s.getStudentName());
        System.out.printf("| Department:   %-35s |%n", s.getDepartment());
        System.out.printf("| Email:        %-35s |%n", s.getEmail());
        System.out.printf("| Phone:        %-35s |%n", s.getPhoneNumber());
        System.out.println("+---------------------------------------------------+");
    }

    private void updateStudentDetails() {
        String id = input.readString("Enter Student ID to update: ");
        Student s = studentService.getStudentById(id);
        displayStudentDetails(s);

        System.out.println("\nEnter new values (press Enter to keep current):");
        String name = input.readOptionalString("New Name", s.getStudentName());
        String dept = input.readOptionalString("New Department", s.getDepartment());
        String email = input.readOptionalString("New Email", s.getEmail());
        String phone = input.readOptionalString("New Phone", s.getPhoneNumber());

        studentService.updateStudent(id, name, dept, email, phone);
        System.out.println("\n  [SUCCESS] Student profile updated.");
        input.waitForEnter();
    }

    private void deleteStudent() {
        String id = input.readString("Enter Student ID to delete: ");
        Student s = studentService.getStudentById(id);
        displayStudentDetails(s);

        if (input.readConfirmation("Are you sure you want to delete this student record?")) {
            studentService.deleteStudent(id);
            System.out.println("\n  [SUCCESS] Student deleted successfully.");
        } else {
            System.out.println("\n  Deletion cancelled.");
        }
        input.waitForEnter();
    }

    // =========================================================================
    // MODULE C — DEMAND MANAGEMENT
    // =========================================================================

    private void handleDemandMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("DEMAND MANAGEMENT");
            System.out.println("  1. Submit Demand Request");
            System.out.println("  2. View All Demands");
            System.out.println("  3. Search Demand by ID");
            System.out.println("  4. View Pending Demands");
            System.out.println("  5. View Issued Demands");
            System.out.println("  6. View Rejected Demands");
            System.out.println("  7. Approve & Issue Demand");
            System.out.println("  8. Reject Demand");
            System.out.println("  9. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-9): ", 1, 9);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> submitDemandRequest();
                    case 2 -> viewDemands(demandService.getAllDemands(), "All Demand Requests");
                    case 3 -> searchDemandById();
                    case 4 -> viewDemands(demandService.getPendingDemands(), "Pending Demands");
                    case 5 -> viewDemands(demandService.getIssuedDemands(), "Issued Demands");
                    case 6 -> viewDemands(demandService.getRejectedDemands(), "Rejected Demands");
                    case 7 -> approveAndIssueDemand();
                    case 8 -> rejectDemand();
                    case 9 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void submitDemandRequest() {
        System.out.println("--- Submit Student Demand Request ---");
        String studentId = input.readString("Enter Student ID: ");
        Student s = studentService.getStudentById(studentId);
        System.out.println("  Student: " + s.getStudentName() + " (" + s.getRollNumber() + ")");

        String itemId = input.readString("Enter Inventory Item ID: ");
        Inventory item = inventoryService.getItemById(itemId);
        System.out.println("  Item: " + item.getItemName() + " | Available Stock: " + item.getAvailableQuantity());

        int qty = input.readInt("Requested Quantity: ");
        String remarks = input.readOptionalString("Purpose / Academic project remarks", "Student demand request");

        DemandRecord demand = demandService.submitDemand(studentId, itemId, qty, remarks);
        System.out.println("\n  [SUCCESS] Demand request registered successfully!");
        System.out.println("  Demand ID: " + demand.getDemandId() + " | Status: " + demand.getStatus());
        input.waitForEnter();
    }

    private void viewDemands(List<DemandRecord> demands, String title) {
        System.out.println("--- " + title + " (" + demands.size() + " total) ---");
        TableFormatter table = new TableFormatter("Demand ID", "Student ID", "Item ID", "Qty", "Request Date", "Status", "Remarks");
        table.setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.CENTER);

        for (DemandRecord d : demands) {
            table.addRow(
                    d.getDemandId(),
                    d.getStudentId(),
                    d.getItemId(),
                    d.getRequestedQuantity(),
                    DateUtils.formatDate(d.getRequestDate()),
                    d.getStatus().getDisplayName(),
                    d.getRemarks()
            );
        }
        table.print();
        input.waitForEnter();
    }

    private void searchDemandById() {
        String id = input.readString("Enter Demand ID: ");
        DemandRecord d = demandService.getDemandById(id);
        System.out.println("\n+---------------------------------------------------+");
        System.out.println("| DEMAND REQUEST DETAILS                            |");
        System.out.println("+---------------------------------------------------+");
        System.out.printf("| Demand ID:      %-33s |%n", d.getDemandId());
        System.out.printf("| Student ID:     %-33s |%n", d.getStudentId());
        System.out.printf("| Item ID:        %-33s |%n", d.getItemId());
        System.out.printf("| Requested Qty:  %-33d |%n", d.getRequestedQuantity());
        System.out.printf("| Request Date:   %-33s |%n", DateUtils.formatDate(d.getRequestDate()));
        System.out.printf("| Status:         %-33s |%n", d.getStatus().getDisplayName());
        System.out.printf("| Remarks:        %-33s |%n", d.getRemarks());
        System.out.println("+---------------------------------------------------+");
        input.waitForEnter();
    }

    private void approveAndIssueDemand() {
        System.out.println("--- Approve & Issue Demand ---");
        List<DemandRecord> pending = demandService.getPendingDemands();
        if (pending.isEmpty()) {
            System.out.println("  No pending demands available to issue.");
            input.waitForEnter();
            return;
        }

        System.out.println("Pending Demands:");
        for (DemandRecord p : pending) {
            Inventory item = inventoryService.getItemById(p.getItemId());
            System.out.printf("  * [%s] Student: %s | Item: %s (%s) | Requested: %d | Available: %d%n",
                    p.getDemandId(), p.getStudentId(), item.getItemName(), item.getItemId(),
                    p.getRequestedQuantity(), item.getAvailableQuantity());
        }

        String demandId = input.readString("\nEnter Demand ID to approve & issue: ");
        String remarks = input.readOptionalString("Approval Remarks", "Approved and issued by lab admin");

        DemandRecord issued = demandService.issueDemand(demandId, remarks);
        Inventory updatedItem = inventoryService.getItemById(issued.getItemId());

        System.out.println("\n  [SUCCESS] Demand issued successfully!");
        System.out.println("  Demand ID: " + issued.getDemandId() + " -> Status: " + issued.getStatus());
        System.out.println("  Remaining Available Stock for " + updatedItem.getItemName() + ": " + updatedItem.getAvailableQuantity() + " units.");
        input.waitForEnter();
    }

    private void rejectDemand() {
        System.out.println("--- Reject Demand Request ---");
        String demandId = input.readString("Enter Demand ID to reject: ");
        String reason = input.readString("Reason for rejection: ");

        DemandRecord rejected = demandService.rejectDemand(demandId, reason);
        System.out.println("\n  [SUCCESS] Demand " + rejected.getDemandId() + " marked as REJECTED.");
        input.waitForEnter();
    }

    // =========================================================================
    // MODULE D — STOCK HISTORY
    // =========================================================================

    private void handleStockHistoryMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("STOCK HISTORY & AUDIT TRAIL");
            System.out.println("  1. View All Stock Transactions");
            System.out.println("  2. Filter Transactions by Item ID");
            System.out.println("  3. View Recent Transactions (Last 10)");
            System.out.println("  4. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-4): ", 1, 4);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> viewHistory(context.getStockHistoryRepository().findAll(), "All Stock Transactions");
                    case 2 -> {
                        String itemId = input.readString("Enter Item ID: ");
                        viewHistory(context.getStockHistoryRepository().findByItemId(itemId), "Transactions for " + itemId);
                    }
                    case 3 -> viewHistory(context.getStockHistoryRepository().findRecent(10), "Recent 10 Transactions");
                    case 4 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void viewHistory(List<StockHistory> list, String title) {
        System.out.println("--- " + title + " (" + list.size() + " total) ---");
        TableFormatter table = new TableFormatter("Txn ID", "Item ID", "Transaction Type", "Qty Delta", "Date", "Audit Remarks");
        table.setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(2, TableFormatter.Alignment.CENTER);

        for (StockHistory h : list) {
            String deltaStr = (h.getQuantityChanged() > 0 ? "+" : "") + h.getQuantityChanged();
            table.addRow(
                    h.getHistoryId(),
                    h.getItemId(),
                    h.getTransactionType().getDisplayName(),
                    deltaStr,
                    DateUtils.formatDate(h.getTransactionDate()),
                    h.getRemarks()
            );
        }
        table.print();
        input.waitForEnter();
    }

    // =========================================================================
    // MODULE E — DEMAND PREDICTION
    // =========================================================================

    private void handlePredictionMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("DEMAND PREDICTION ENGINE");
            System.out.println("  1. Predict Demand for an Item");
            System.out.println("  2. Run Predictions for All Items");
            System.out.println("  3. Record Past Monthly Historical Demand");
            System.out.println("  4. View Historical Records for an Item");
            System.out.println("  5. View Prediction History");
            System.out.println("  6. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-6): ", 1, 6);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> predictDemandForItem();
                    case 2 -> runPredictionsForAll();
                    case 3 -> recordHistoricalDemand();
                    case 4 -> viewHistoricalRecordsForItem();
                    case 5 -> viewPredictionHistory();
                    case 6 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void predictDemandForItem() {
        System.out.println("--- Generate 3-Month Moving Average Demand Prediction ---");
        String itemId = input.readString("Enter Item ID: ");
        Inventory item = inventoryService.getItemById(itemId);

        YearMonth defaultTarget = YearMonth.now().plusMonths(1);
        YearMonth target = input.readYearMonth("Target Prediction Month", defaultTarget);

        Prediction p = predictionService.predictDemandForItem(itemId, target);
        renderPredictionDetails(item, p);
        input.waitForEnter();
    }

    private void renderPredictionDetails(Inventory item, Prediction p) {
        System.out.println("\n+---------------------------------------------------------------------------------+");
        System.out.println("| DEMAND PREDICTION & RESTOCKING REPORT                                            |");
        System.out.println("+---------------------------------------------------------------------------------+");
        System.out.printf("| Item:                 %-56s |%n", item.getItemName() + " (" + item.getItemId() + ")");
        System.out.printf("| Target Forecast:      %-56s |%n", DateUtils.formatYearMonth(p.getTargetYearMonth()));
        System.out.printf("| Data Coverage Status: %-56s |%n", p.getStatus().getDisplayName());
        System.out.println("+---------------------------------------------------------------------------------+");
        System.out.println("| 3-MONTH HISTORICAL BASELINE EVALUATION:                                         |");

        int idx = 1;
        for (Prediction.MonthConsumption mc : p.getReferenceMonths()) {
            String status = mc.isCovered()
                    ? String.format("%d units [%s]", mc.getConsumedQuantity(), mc.getSourceDescription())
                    : "NO DATA (Missing Coverage)";
            System.out.printf("|   Month %d (%s): %-53s |%n", idx++, DateUtils.formatYearMonth(mc.getYearMonth()), status);
        }

        System.out.println("+---------------------------------------------------------------------------------+");
        if (p.getStatus() == PredictionStatus.SUFFICIENT) {
            System.out.printf("| Predicted Demand:     %-56.2f |%n", p.getPredictedDemand());
            System.out.printf("| Available Stock:      %-56d |%n", item.getAvailableQuantity());
            System.out.printf("| Safety Stock (Min):   %-56d |%n", item.getMinimumStock());
            System.out.printf("| High Demand Warning:  %-56s |%n", p.isHighDemand() ? "YES (Predicted Demand > Current Stock)" : "No");
            System.out.printf("| Suggested Restock:    %-56s |%n", p.getSuggestedRestockQuantity() + " units [MAX(0, ceil(Pred + Min - Stock))]");
        } else {
            System.out.println("| [!] CANNOT CALCULATE PREDICTION: Complete 3-month coverage is missing.          |");
            System.out.println("|     To enable forecasting, record consumption via option 3 in this menu.       |");
        }
        System.out.println("+---------------------------------------------------------------------------------+");
        System.out.println("| Algorithm Explanation:                                                          |");
        System.out.printf("| %-79s |%n", p.getExplanation());
        System.out.println("+---------------------------------------------------------------------------------+");
    }

    private void runPredictionsForAll() {
        YearMonth defaultTarget = YearMonth.now().plusMonths(1);
        YearMonth target = input.readYearMonth("Target Prediction Month", defaultTarget);

        List<Prediction> predictions = predictionService.predictForAllItems(target);

        System.out.println("\n--- All Inventory Predictions for " + DateUtils.formatYearMonth(target) + " ---");
        TableFormatter table = new TableFormatter("Item ID", "Item Name", "Coverage", "Predicted", "Stock", "Safety", "High Demand", "Restock Qty");
        table.setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(4, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.RIGHT)
             .setAlignment(6, TableFormatter.Alignment.CENTER)
             .setAlignment(7, TableFormatter.Alignment.RIGHT);

        for (Prediction p : predictions) {
            Inventory item = inventoryService.getItemById(p.getItemId());
            String predStr = (p.getStatus() == PredictionStatus.SUFFICIENT) ? String.format("%.1f", p.getPredictedDemand()) : "N/A";
            String restockStr = (p.getStatus() == PredictionStatus.SUFFICIENT) ? String.valueOf(p.getSuggestedRestockQuantity()) : "N/A";

            table.addRow(
                    p.getItemId(),
                    item.getItemName(),
                    p.getStatus() == PredictionStatus.SUFFICIENT ? "Sufficient" : "Insufficient",
                    predStr,
                    item.getAvailableQuantity(),
                    item.getMinimumStock(),
                    p.isHighDemand() ? "YES" : "No",
                    restockStr
            );
        }
        table.print();
        input.waitForEnter();
    }

    private void recordHistoricalDemand() {
        System.out.println("--- Record Historical Monthly Consumption ---");
        String itemId = input.readString("Enter Item ID: ");
        Inventory item = inventoryService.getItemById(itemId);
        System.out.println("  Item: " + item.getItemName());

        int year = input.readInt("Historical Year (e.g. 2026): ", 2000, 2100);
        int month = input.readInt("Historical Month (1-12): ", 1, 12);
        int qty = input.readInt("Actual Consumed Quantity in that month: ");
        String remarks = input.readOptionalString("Remarks / Context", "Lab course consumption");

        HistoricalDemand hd = predictionService.recordHistoricalDemand(itemId, year, month, qty, remarks);
        System.out.println("\n  [SUCCESS] Historical demand recorded successfully!");
        System.out.println("  Item: " + item.getItemName() + " | Period: " + DateUtils.formatYearMonth(hd.getYearMonth()) + " | Consumed: " + hd.getConsumedQuantity() + " units.");
        System.out.println("  (Note: Active stock was NOT modified)");
        input.waitForEnter();
    }

    private void viewHistoricalRecordsForItem() {
        String itemId = input.readString("Enter Item ID: ");
        Inventory item = inventoryService.getItemById(itemId);
        List<HistoricalDemand> list = predictionService.getHistoricalDemandForItem(itemId);

        System.out.println("--- Historical Consumption Records for " + item.getItemName() + " (" + itemId + ") ---");
        TableFormatter table = new TableFormatter("Record ID", "Period", "Quantity Consumed", "Remarks");
        table.setAlignment(2, TableFormatter.Alignment.RIGHT);

        for (HistoricalDemand hd : list) {
            table.addRow(hd.getId(), DateUtils.formatYearMonth(hd.getYearMonth()), hd.getConsumedQuantity(), hd.getRemarks());
        }
        table.print();
        input.waitForEnter();
    }

    private void viewPredictionHistory() {
        List<Prediction> list = predictionService.getPredictionHistory();
        System.out.println("--- Generated Prediction History (" + list.size() + " records) ---");
        TableFormatter table = new TableFormatter("Pred ID", "Item ID", "Target Month", "Coverage", "Predicted", "Restock", "Date Run");
        table.setAlignment(4, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.RIGHT);

        for (Prediction p : list) {
            table.addRow(
                    p.getPredictionId(),
                    p.getItemId(),
                    DateUtils.formatYearMonth(p.getTargetYearMonth()),
                    p.getStatus().getDisplayName(),
                    p.getStatus() == PredictionStatus.SUFFICIENT ? String.format("%.2f", p.getPredictedDemand()) : "N/A",
                    p.getStatus() == PredictionStatus.SUFFICIENT ? p.getSuggestedRestockQuantity() : "N/A",
                    DateUtils.formatDate(p.getPredictionDate())
            );
        }
        table.print();
        input.waitForEnter();
    }

    // =========================================================================
    // MODULE F — STOCK ALERTS
    // =========================================================================

    private void handleStockAlertsMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("STOCK ALERTS & WARNINGS");
            System.out.println("  1. View All Active Alerts");
            System.out.println("  2. View Low-Stock Alerts");
            System.out.println("  3. View Out-of-Stock Alerts");
            System.out.println("  4. View High-Demand Items (Predicted > Available)");
            System.out.println("  5. Restocking Recommendations");
            System.out.println("  6. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-6): ", 1, 6);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> renderAlertsTable(stockAlertService.generateAllAlerts(), "All Active Alerts");
                    case 2 -> renderAlertsTable(stockAlertService.getLowStockAlerts(), "Low-Stock Alerts");
                    case 3 -> renderAlertsTable(stockAlertService.getOutOfStockAlerts(), "Out-of-Stock Alerts");
                    case 4 -> renderAlertsTable(stockAlertService.getHighDemandAlerts(), "High-Demand Alerts");
                    case 5 -> renderRestockingRecommendations();
                    case 6 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void renderAlertsTable(List<StockAlertItem> alerts, String title) {
        System.out.println("--- " + title + " (" + alerts.size() + " items) ---");
        TableFormatter table = new TableFormatter("Item ID", "Item Name", "Current Stock", "Min Stock", "Status", "Pred Demand", "Suggested Restock", "Alert Details");
        table.setAlignment(2, TableFormatter.Alignment.RIGHT)
             .setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.RIGHT)
             .setAlignment(6, TableFormatter.Alignment.RIGHT);

        for (StockAlertItem a : alerts) {
            String predStr = (a.getPredictedDemand() != null) ? String.format("%.1f", a.getPredictedDemand()) : "N/A";
            String restockStr = (a.getSuggestedRestock() != null) ? String.valueOf(a.getSuggestedRestock()) : "N/A";

            table.addRow(
                    a.getItemId(),
                    a.getItemName(),
                    a.getCurrentStock(),
                    a.getMinimumStock(),
                    a.getStockStatus().getDisplayName(),
                    predStr,
                    restockStr,
                    a.getAlertMessage()
            );
        }
        table.print();
        input.waitForEnter();
    }

    private void renderRestockingRecommendations() {
        List<StockAlertItem> list = stockAlertService.generateAllAlerts().stream()
                .filter(a -> a.getSuggestedRestock() != null && a.getSuggestedRestock() > 0)
                .toList();

        System.out.println("--- Restocking Recommendations (" + list.size() + " items requiring restock) ---");
        TableFormatter table = new TableFormatter("Item ID", "Item Name", "Stock", "Min Stock", "Pred Demand", "Restock Qty", "Unit Price", "Est. Cost");
        table.setAlignment(2, TableFormatter.Alignment.RIGHT)
             .setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(4, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.RIGHT)
             .setAlignment(6, TableFormatter.Alignment.RIGHT)
             .setAlignment(7, TableFormatter.Alignment.RIGHT);

        double totalCost = 0.0;
        for (StockAlertItem a : list) {
            Inventory item = inventoryService.getItemById(a.getItemId());
            double cost = a.getSuggestedRestock() * item.getPrice();
            totalCost += cost;
            table.addRow(
                    a.getItemId(),
                    a.getItemName(),
                    a.getCurrentStock(),
                    a.getMinimumStock(),
                    String.format("%.1f", a.getPredictedDemand()),
                    a.getSuggestedRestock(),
                    String.format("%.2f", item.getPrice()),
                    String.format("%.2f", cost)
            );
        }
        table.print();
        System.out.printf("  Estimated Total Restocking Procurement Cost: $%.2f%n", totalCost);
        input.waitForEnter();
    }

    // =========================================================================
    // MODULE G — DASHBOARD AND REPORTS
    // =========================================================================

    private void handleReportsMenu() {
        boolean back = false;
        while (!back) {
            printBrandHeader("DASHBOARD AND ANALYTICS REPORTS");
            System.out.println("  1. System Executive Dashboard (Live Metrics)");
            System.out.println("  2. Inventory Valuation & Health Report");
            System.out.println("  3. Student Directory Summary");
            System.out.println("  4. Demand Status Breakdown Report");
            System.out.println("  5. Monthly Demand Summary Report");
            System.out.println("  6. Stock Movement Report");
            System.out.println("  7. Return to Main Menu");
            System.out.println("====================================================");

            int choice = input.readInt("Enter your choice (1-7): ", 1, 7);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> showExecutiveDashboard();
                    case 2 -> showInventoryReport();
                    case 3 -> showStudentReport();
                    case 4 -> showDemandReport();
                    case 5 -> showMonthlyDemandReport();
                    case 6 -> showStockMovementReport();
                    case 7 -> back = true;
                }
            } catch (StockWiseException e) {
                System.out.println("\n  [ERROR] " + e.getMessage());
                input.waitForEnter();
            }
        }
    }

    private void showExecutiveDashboard() {
        ReportService.DashboardMetrics m = reportService.getDashboardMetrics();

        System.out.println("===============================================================================");
        System.out.println("                        STOCKWISE EXECUTIVE DASHBOARD                          ");
        System.out.println("===============================================================================");
        System.out.println("  INVENTORY METRICS:                          STUDENT & DEMAND METRICS:       ");
        System.out.printf("   Total Inventory Items:     %-14d   Total Students:            %-12d%n", m.totalItems, m.totalStudents);
        System.out.printf("   Total Available Stock:     %-14d   Pending Demands:           %-12d%n", m.totalAvailableStock, m.totalPendingDemands);
        System.out.printf("   Total Inventory Value:    $%-14.2f   Issued Demands:            %-12d%n", m.totalInventoryValuation, m.totalIssuedDemands);
        System.out.printf("   Low-Stock Items:           %-14d   Rejected Demands:          %-12d%n", m.lowStockCount, m.totalRejectedDemands);
        System.out.printf("   Out-of-Stock Items:        %-14d   Current Month Issued Units:%-12d%n", m.outOfStockCount, m.currentMonthIssuedUnits);
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("  PREDICTIVE DEMAND INSIGHTS (NEXT MONTH):");
        System.out.printf("   High-Demand Risk Items:    %-14d%n", m.highDemandCount);
        if (!m.highDemandItemNames.isEmpty()) {
            for (String risk : m.highDemandItemNames) {
                System.out.println("    [!] " + risk);
            }
        } else {
            System.out.println("    [OK] No immediate high-demand stockout risks detected.");
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("  RECENT STOCK AUDIT MOVEMENTS:");
        if (m.recentTransactions.isEmpty()) {
            System.out.println("    (No stock movements recorded yet)");
        } else {
            for (StockHistory h : m.recentTransactions) {
                System.out.printf("    - [%s] %s | %s | %s%s units | %s%n",
                        h.getHistoryId(), DateUtils.formatDate(h.getTransactionDate()),
                        h.getItemId(), (h.getQuantityChanged() > 0 ? "+" : ""),
                        h.getQuantityChanged(), h.getRemarks());
            }
        }
        System.out.println("===============================================================================");
        input.waitForEnter();
    }

    private void showInventoryReport() {
        List<Inventory> list = inventoryService.getAllItems();
        System.out.println("--- Comprehensive Inventory Valuation & Status Report ---");
        TableFormatter table = new TableFormatter("Item ID", "Item Name", "Category", "Price", "Stock", "Min", "Valuation", "Status");
        table.setAlignment(3, TableFormatter.Alignment.RIGHT)
             .setAlignment(4, TableFormatter.Alignment.RIGHT)
             .setAlignment(5, TableFormatter.Alignment.RIGHT)
             .setAlignment(6, TableFormatter.Alignment.RIGHT)
             .setAlignment(7, TableFormatter.Alignment.CENTER);

        double totalVal = 0.0;
        int totalUnits = 0;
        for (Inventory i : list) {
            double val = i.getAvailableQuantity() * i.getPrice();
            totalVal += val;
            totalUnits += i.getAvailableQuantity();
            table.addRow(
                    i.getItemId(),
                    i.getItemName(),
                    i.getCategory(),
                    String.format("%.2f", i.getPrice()),
                    i.getAvailableQuantity(),
                    i.getMinimumStock(),
                    String.format("%.2f", val),
                    i.getStockStatus().getDisplayName()
            );
        }
        table.print();
        System.out.printf("  Total Catalog SKUs: %d | Total Active Units: %d | Total Capital Valuation: $%.2f%n",
                list.size(), totalUnits, totalVal);
        input.waitForEnter();
    }

    private void showStudentReport() {
        List<Student> list = studentService.getAllStudents();
        System.out.println("--- Student Directory & Engagement Report ---");
        TableFormatter table = new TableFormatter("Student ID", "Roll No", "Full Name", "Department", "Demands Logged");
        table.setAlignment(4, TableFormatter.Alignment.RIGHT);

        for (Student s : list) {
            int dmdCount = demandService.getDemandsByStudent(s.getStudentId()).size();
            table.addRow(s.getStudentId(), s.getRollNumber(), s.getStudentName(), s.getDepartment(), dmdCount);
        }
        table.print();
        input.waitForEnter();
    }

    private void showDemandReport() {
        System.out.println("--- Student Demand Lifecycle Summary ---");
        System.out.println("  Pending Demands:  " + demandService.getPendingCount());
        System.out.println("  Issued Demands:   " + demandService.getIssuedCount());
        System.out.println("  Rejected Demands: " + demandService.getRejectedCount());
        System.out.println("  Total Demands:    " + demandService.getTotalDemandCount());
        System.out.println();

        viewDemands(demandService.getAllDemands(), "Demand Audit List");
    }

    private void showMonthlyDemandReport() {
        List<ReportService.MonthlyDemandAggregate> list = reportService.getMonthlyDemandSummary();
        System.out.println("--- Monthly Aggregated Demand Consumption Report ---");
        TableFormatter table = new TableFormatter("Calendar Month", "Issued Demands (Units)", "Historical Records (Units)", "Total Consumed Units");
        table.setAlignment(1, TableFormatter.Alignment.RIGHT)
             .setAlignment(2, TableFormatter.Alignment.RIGHT)
             .setAlignment(3, TableFormatter.Alignment.RIGHT);

        for (ReportService.MonthlyDemandAggregate m : list) {
            table.addRow(DateUtils.formatYearMonth(m.yearMonth), m.issuedDemandUnits, m.historicalDemandUnits, m.totalConsumedUnits);
        }
        table.print();
        input.waitForEnter();
    }

    private void showStockMovementReport() {
        List<StockHistory> all = context.getStockHistoryRepository().findAll();
        System.out.println("--- Stock Movement & Audit Trail Summary (" + all.size() + " total movements) ---");
        viewHistory(all, "Stock Audit Movements");
    }
}
