package com.stockwise.storage;

import com.stockwise.model.DemandStatus;
import com.stockwise.model.Inventory;
import com.stockwise.model.Student;
import com.stockwise.repository.DemandRepository;
import com.stockwise.repository.HistoricalDemandRepository;
import com.stockwise.repository.InMemoryDemandRepository;
import com.stockwise.repository.InMemoryHistoricalDemandRepository;
import com.stockwise.repository.InMemoryInventoryRepository;
import com.stockwise.repository.InMemoryPredictionRepository;
import com.stockwise.repository.InMemoryStockHistoryRepository;
import com.stockwise.repository.InMemoryStudentRepository;
import com.stockwise.repository.InventoryRepository;
import com.stockwise.repository.PredictionRepository;
import com.stockwise.repository.StockHistoryRepository;
import com.stockwise.repository.StudentRepository;
import com.stockwise.service.DemandService;
import com.stockwise.service.InventoryService;
import com.stockwise.service.PredictionService;
import com.stockwise.service.ReportService;
import com.stockwise.service.StockAlertService;
import com.stockwise.service.StudentService;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Shared Application Context implementing Inversion of Control (IoC).
 * Guarantees single-source-of-truth repository state across all business services.
 * Assigned to: Janvi (Shared Data Architecture)
 */
public class AppContext {

    // Repositories (Contracts)
    private final InventoryRepository inventoryRepository;
    private final StudentRepository studentRepository;
    private final DemandRepository demandRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final HistoricalDemandRepository historicalDemandRepository;
    private final PredictionRepository predictionRepository;

    // Services
    private final InventoryService inventoryService;
    private final StudentService studentService;
    private final DemandService demandService;
    private final PredictionService predictionService;
    private final StockAlertService stockAlertService;
    private final ReportService reportService;

    public AppContext() {
        // 1. Initialize shared in-memory repositories
        this.inventoryRepository = new InMemoryInventoryRepository();
        this.studentRepository = new InMemoryStudentRepository();
        this.demandRepository = new InMemoryDemandRepository();
        this.stockHistoryRepository = new InMemoryStockHistoryRepository();
        this.historicalDemandRepository = new InMemoryHistoricalDemandRepository();
        this.predictionRepository = new InMemoryPredictionRepository();

        // 2. Wire services with shared repository instances
        this.inventoryService = new InventoryService(
                this.inventoryRepository,
                this.stockHistoryRepository,
                this.demandRepository
        );

        this.studentService = new StudentService(
                this.studentRepository,
                this.demandRepository
        );

        this.demandService = new DemandService(
                this.demandRepository,
                this.inventoryRepository,
                this.studentRepository,
                this.stockHistoryRepository
        );

        this.predictionService = new PredictionService(
                this.inventoryRepository,
                this.demandRepository,
                this.historicalDemandRepository,
                this.predictionRepository,
                this.stockHistoryRepository
        );

        this.stockAlertService = new StockAlertService(
                this.inventoryRepository,
                this.predictionService
        );

        this.reportService = new ReportService(
                this.inventoryRepository,
                this.studentRepository,
                this.demandRepository,
                this.stockHistoryRepository,
                this.historicalDemandRepository,
                this.predictionService
        );
    }

    /**
     * Seeds initial academic inventory, students, historical consumption, and demand records.
     * Provides an out-of-the-box rich dataset for college viva demonstrations.
     */
    public void seedInitialData() {
        // --- 1. Seed Inventory Items ---
        inventoryService.addItem("ITM-1001", "Arduino Uno R3", "Lab Equipment",
                "Microcontroller board for IoT & Embedded Systems lab", 25, 550.00, 10);

        inventoryService.addItem("ITM-1002", "Breadboard 830-Point", "Electronics",
                "Solderless breadboard for circuit prototyping", 8, 120.00, 15); // LOW STOCK

        inventoryService.addItem("ITM-1003", "Raspberry Pi 4 Model B", "Computing",
                "4GB RAM Single Board Computer for AI/Edge projects", 0, 4500.00, 5); // OUT OF STOCK

        inventoryService.addItem("ITM-1004", "Digital Multimeter", "Lab Equipment",
                "Autoranging digital multimeter with temperature sensor", 30, 850.00, 10);

        inventoryService.addItem("ITM-1005", "Jumper Wire Pack (65 pcs)", "Electronics",
                "Male-to-male and male-to-female flexible jumper cables", 12, 150.00, 20); // LOW STOCK

        inventoryService.addItem("ITM-1006", "Soldering Iron Kit (60W)", "Tools",
                "Adjustable temperature soldering station kit", 18, 750.00, 8); // INSUFFICIENT HISTORY by default

        // --- 2. Seed Students ---
        studentService.addStudent("STU-1001", "Harshita Sharma", "CS2024-001",
                "Computer Science", "harshita.sharma@college.edu", "9876543210");

        studentService.addStudent("STU-1002", "Janvi Verma", "CS2024-002",
                "Information Technology", "janvi.verma@college.edu", "9876543211");

        studentService.addStudent("STU-1003", "Anmol Singh", "ECE2024-015",
                "Electronics & Comm", "anmol.singh@college.edu", "9876543212");

        studentService.addStudent("STU-1004", "Sedhat Khan", "ME2024-022",
                "Mechanical Engg", "sedhat.khan@college.edu", "9876543213");

        studentService.addStudent("STU-1005", "Priya Patel", "CS2024-035",
                "Computer Science", "priya.patel@college.edu", "9876543214");

        // --- 3. Seed Historical Monthly Consumption ---
        // Dynamically compute the three previous completed calendar months relative to next month
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        YearMonth m1 = nextMonth.minusMonths(3);
        YearMonth m2 = nextMonth.minusMonths(2);
        YearMonth m3 = nextMonth.minusMonths(1);

        // ITM-1001: Historical demand: m1=15, m2=20, m3=25 -> Average = 20.0
        predictionService.recordHistoricalDemand("ITM-1001", m1.getYear(), m1.getMonthValue(), 15, "Semester lab consumption");
        predictionService.recordHistoricalDemand("ITM-1001", m2.getYear(), m2.getMonthValue(), 20, "Semester lab consumption");
        predictionService.recordHistoricalDemand("ITM-1001", m3.getYear(), m3.getMonthValue(), 25, "Project exhibition preparation");

        // ITM-1002: Historical demand: m1=20, m2=25, m3=30 -> Average = 25.0
        // Current Stock = 8, Min Stock = 15 -> Restock = 25 + 15 - 8 = 32 units!
        predictionService.recordHistoricalDemand("ITM-1002", m1.getYear(), m1.getMonthValue(), 20, "Introductory Electronics workshop");
        predictionService.recordHistoricalDemand("ITM-1002", m2.getYear(), m2.getMonthValue(), 25, "Mid-term circuit lab");
        predictionService.recordHistoricalDemand("ITM-1002", m3.getYear(), m3.getMonthValue(), 30, "Hardware hackathon requirements");

        // ITM-1004: Multimeter: m1=5, m2=5, m3=5 -> Average = 5.0
        predictionService.recordHistoricalDemand("ITM-1004", m1.getYear(), m1.getMonthValue(), 5, "Physics & Basic Elec labs");
        predictionService.recordHistoricalDemand("ITM-1004", m2.getYear(), m2.getMonthValue(), 5, "Instrumentation lab");
        predictionService.recordHistoricalDemand("ITM-1004", m3.getYear(), m3.getMonthValue(), 5, "Project phase 1 testing");

        // ITM-1005: Wires: m1=30, m2=40, m3=50 -> Average = 40.0
        predictionService.recordHistoricalDemand("ITM-1005", m1.getYear(), m1.getMonthValue(), 30, "Robotics club projects");
        predictionService.recordHistoricalDemand("ITM-1005", m2.getYear(), m2.getMonthValue(), 40, "Robotics club projects");
        predictionService.recordHistoricalDemand("ITM-1005", m3.getYear(), m3.getMonthValue(), 50, "Annual project demo");

        // ITM-1006 (Soldering kit) deliberately has ONLY m1 seeded, leaving m2 and m3 missing,
        // allowing students to demonstrate INSUFFICIENT HISTORY detection!
        predictionService.recordHistoricalDemand("ITM-1006", m1.getYear(), m1.getMonthValue(), 10, "Workshop tools usage");

        // --- 4. Seed Demands & Issued Records ---
        // A pending demand for ITM-1001
        demandService.submitDemand("STU-1001", "ITM-1001", 3, "Mini-project IoT sensor node");
        // A pending demand for ITM-1002
        demandService.submitDemand("STU-1003", "ITM-1002", 2, "Analog filters experiment");
        // A demand that was approved and issued
        var issuedDmd1 = demandService.submitDemand("STU-1002", "ITM-1004", 2, "Digital electronics lab practical");
        demandService.issueDemand(issuedDmd1.getDemandId(), "Approved by Lab In-Charge Dr. Verma");
        // A demand that was rejected
        var rejectedDmd1 = demandService.submitDemand("STU-1004", "ITM-1003", 4, "Personal weekend project");
        demandService.rejectDemand(rejectedDmd1.getDemandId(), "Personal projects not covered by lab allocation");
    }

    // Getters for Repositories
    public InventoryRepository getInventoryRepository() { return inventoryRepository; }
    public StudentRepository getStudentRepository() { return studentRepository; }
    public DemandRepository getDemandRepository() { return demandRepository; }
    public StockHistoryRepository getStockHistoryRepository() { return stockHistoryRepository; }
    public HistoricalDemandRepository getHistoricalDemandRepository() { return historicalDemandRepository; }
    public PredictionRepository getPredictionRepository() { return predictionRepository; }

    // Getters for Services
    public InventoryService getInventoryService() { return inventoryService; }
    public StudentService getStudentService() { return studentService; }
    public DemandService getDemandService() { return demandService; }
    public PredictionService getPredictionService() { return predictionService; }
    public StockAlertService getStockAlertService() { return stockAlertService; }
    public ReportService getReportService() { return reportService; }
}
