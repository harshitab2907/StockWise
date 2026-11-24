# StockWise — Student Inventory Demand Predictor
> **Smart Inventory. Smarter Decisions.**

An academic Java console application engineered for college laboratory and department inventory management, featuring intelligent demand forecasting using explainable 3-month moving average calculations.

Developed as an academic software engineering project by a 4-member undergraduate computer science team:
- **Harshita** — Demand Prediction & Forecasting
- **Janvi** — Shared Data Architecture & Repository Contracts
- **Anmol** — Inventory Management & Stock Alerts
- **Sedhat** — Student & Demand Request Management

---

## 1. Project Introduction

In educational institutions, college departments maintain critical physical inventories such as microcontrollers (Arduino, Raspberry Pi), lab measuring equipment (multimeters, oscilloscopes), electrical prototyping tools (breadboards, soldering stations), and consumable electronic components (ICs, sensor packs, jumper cables). 

Managing these supplies manually or through disconnected spreadsheets frequently causes:
- Sudden stockouts during peak semester exam or mini-project submission periods.
- Over-purchasing of redundant supplies, resulting in wasted departmental budgets.
- Lack of accountability when issuing lab equipment to students.
- Inability to anticipate how many components will be required next month.

**StockWise** bridges this gap. It provides a cohesive, menu-driven Java management application that coordinates inventory levels, student requests, and historical consumption, backed by a deterministic, mathematically explainable demand predictor.

---

## 2. Problem Statement

College laboratory administrators face recurring challenges in predicting student demand for hardware components. Traditional inventory systems are either purely descriptive (showing current stock without future foresight) or rely on complex "black-box" machine learning algorithms that cannot be transparently explained or verified during academic audits.

Furthermore, students often request lab components without prior stock verification, leading to double-allocation and inventory inaccuracies. StockWise solves these issues by:
1. Enforcing atomic stock deductions upon request issuance.
2. Distinguishing known zero-demand periods (e.g., semester vacations) from unknown periods with missing data.
3. Formulating explainable 3-month moving average predictions coupled with dynamic restocking recommendations.

---

## 3. Project Objectives

1. **Demonstrate Object-Oriented Programming (OOP):** Leverage encapsulation, clean abstractions, domain separation, and custom exception handling using standard Java 17.
2. **Provide Single Source of Truth:** Guarantee that all modules share identical repository instances via a centralized `AppContext`.
3. **Deterministic Demand Forecasting:** Implement an explainable 3-month moving average algorithm that detects missing historical coverage and prevents erroneous zero substitutions.
4. **Reliable Stock Audit Trail:** Ensure all stock changes (allocations, issuances, adjustments, historical records) are recorded in an audit trail.
5. **Future-Proof Repository Architecture:** Prepare clean repository interfaces so that MySQL and JDBC persistence can be plugged in during Evaluation 2 with zero changes to business services.

---

## 4. Academic Evaluation Roadmap: Evaluation 1 vs. Evaluation 2

| Dimension | Evaluation 1 (Current Version) | Evaluation 2 (Upcoming Milestone) |
|---|---|---|
| **Storage Engine** | In-Memory (`ArrayList`, `LinkedHashMap`, Java Collections) | Relational Database (MySQL 8.x via JDBC) |
| **Data Lifecycle** | Volatile in-memory; resets upon program exit; seeds pre-populated demo data on startup | Persistent disk storage across sessions |
| **Architecture** | Repository Interface Contracts implemented by in-memory storage classes | Same Repository Contracts implemented by JDBC/DAO classes |
| **Service Layer** | `InventoryService`, `DemandService`, `PredictionService` depend only on repository contracts | 100% reused without modifying any business logic |
| **User Interface** | Cross-platform ANSI/ASCII Console Interface | Console Interface / Optional JavaFX GUI |

> **Important Note for Examiners:** Per Professor's instructions for Evaluation 1, strictly no JDBC, MySQL, Tomcat, or web frameworks are included. The in-memory architecture has been intentionally engineered with repository interfaces so database connectivity can be introduced in Evaluation 2 without refactoring business logic.

---

## 5. Technology Stack

- **Programming Language:** Java 17 LTS (Amazon Corretto / Oracle OpenJDK / OpenJDK 17+)
- **Build & Execution Tools:** Standard `javac` and `java` CLI, Bash helper scripts (`compile.sh`, `run.sh`, `test.sh`)
- **Persistence (Eval 1):** In-Memory thread-safe Java Collections (`LinkedHashMap`, `ArrayList`, `TreeMap`)
- **Date & Calendar API:** `java.time.LocalDate`, `java.time.YearMonth`, `java.time.format.DateTimeFormatter`
- **Testing Framework:** Standalone custom Java assertion engine (`TestAssertions`, `TestRunner`), zero external testing dependencies
- **External Dependencies:** **NONE (0 external libraries or third-party JARs)**

---

## 6. Project Architecture

StockWise adheres to a modular **4-Tier Layered Architecture**:

```
+-------------------------------------------------------------+
|                         UI LAYER                            |
|        ConsoleMenu.java   |   ConsoleInput.java             |
|        TableFormatter.java (Cross-Platform ASCII Tables)    |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                       SERVICE LAYER                         |
|   InventoryService      |   StudentService                  |
|   DemandService         |   PredictionService               |
|   StockAlertService     |   ReportService                   |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                 REPOSITORY CONTRACTS LAYER                  |
|   InventoryRepository   |   StudentRepository               |
|   DemandRepository      |   StockHistoryRepository          |
|   PredictionRepository  |   HistoricalDemandRepository      |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|           STORAGE / APPLICATION CONTEXT LAYER               |
|   AppContext.java (Dependency Injection & Shared State)     |
|   InMemory*Repository implementations (Collections)         |
+-------------------------------------------------------------+
```

### Shared Application Context (`AppContext.java`)
All business services interact with **shared repository singletons** instantiated by `AppContext`. 
- Adding an item in Inventory Management immediately makes it visible in Student Demand requests.
- Approving and issuing a student demand immediately updates active inventory stock, records a transaction in stock history, and updates the executive dashboard metrics.
- All modules query the same live data store.

---

## 7. Folder Structure

```
StockWise/
├── src/
│   └── com/
│       └── stockwise/
│           ├── Main.java                             # Application entry point
│           ├── exception/                            # Custom unchecked domain exceptions
│           │   ├── DuplicateResourceException.java
│           │   ├── InsufficientStockException.java
│           │   ├── ResourceNotFoundException.java
│           │   ├── StockWiseException.java
│           │   └── ValidationException.java
│           ├── model/                                # Pure domain models & business enums
│           │   ├── DemandRecord.java
│           │   ├── DemandStatus.java                 # PENDING, ISSUED, REJECTED
│           │   ├── HistoricalDemand.java
│           │   ├── Inventory.java
│           │   ├── Prediction.java
│           │   ├── PredictionStatus.java             # SUFFICIENT, INSUFFICIENT_HISTORY
│           │   ├── StockHistory.java
│           │   ├── StockStatus.java                  # IN_STOCK, LOW_STOCK, OUT_OF_STOCK
│           │   └── TransactionType.java              # STOCK_ADDED, STOCK_ISSUED, STOCK_ADJUSTED, HISTORICAL_ENTRY
│           ├── repository/                           # Interface contracts & in-memory implementations
│           │   ├── DemandRepository.java
│           │   ├── HistoricalDemandRepository.java
│           │   ├── InMemoryDemandRepository.java
│           │   ├── InMemoryHistoricalDemandRepository.java
│           │   ├── InMemoryInventoryRepository.java
│           │   ├── InMemoryPredictionRepository.java
│           │   ├── InMemoryStockHistoryRepository.java
│           │   ├── InMemoryStudentRepository.java
│           │   ├── InventoryRepository.java
│           │   ├── PredictionRepository.java
│           │   ├── StockHistoryRepository.java
│           │   └── StudentRepository.java
│           ├── service/                              # Core business logic and validation
│           │   ├── DemandService.java
│           │   ├── InventoryService.java
│           │   ├── PredictionService.java
│           │   ├── ReportService.java
│           │   ├── StockAlertService.java
│           │   └── StudentService.java
│           ├── storage/                              # State management & IoC context
│           │   └── AppContext.java
│           ├── ui/                                   # Console presentation & table formatting
│           │   ├── ConsoleInput.java
│           │   ├── ConsoleMenu.java
│           │   └── TableFormatter.java
│           └── util/                                 # Shared utilities & sequence generators
│               ├── DateUtils.java
│               ├── IDGenerator.java
│               └── ValidationUtils.java
│
├── test/
│   └── com/
│       └── stockwise/                                # Automated unit test suites
│           ├── DemandServiceTest.java
│           ├── InventoryServiceTest.java
│           ├── PredictionServiceTest.java
│           ├── StockHistoryTest.java
│           ├── StudentServiceTest.java
│           ├── TestAssertions.java                   # Zero-dependency test assertions
│           └── TestRunner.java                       # Master test runner
│
├── PROJECT_CONTRACTS.md                              # Detailed interface specifications
├── README.md                                         # Project manual & viva guide
├── .gitignore                                        # Clean Git version control exclusions
├── compile.sh                                        # Compilation script
├── run.sh                                            # Application launch script
└── test.sh                                           # Automated test runner script
```

---

## 8. Detailed Module Descriptions

### Module A: Inventory Management (Anmol)
- Manages SKU catalogs with Item ID, Name, Category, Description, Available Quantity, Unit Price, Minimum Stock Threshold (Safety Stock), and Date Added.
- Computes dynamic stock health:
  - `IN STOCK`: $\text{Available Quantity} > \text{Minimum Stock}$
  - `LOW STOCK`: $0 < \text{Available Quantity} \le \text{Minimum Stock}$
  - `OUT OF STOCK`: $\text{Available Quantity} = 0$
- Supports adding stock (replenishment) and manual stock adjustment (physical audit corrections) with automatic audit entries.
- Rejects deleting items that have dependent demand records.

### Module B: Student Management (Sedhat)
- Enrolls students with Student ID, Full Name, Roll Number, Academic Department, Email, and Phone.
- Enforces uniqueness constraints on both Student ID and Roll Number.
- Validates email (`user@domain.com`) and phone number formatting.
- Prevents unsafe deletion of students who have active or past demand records.

### Module C: Demand Management (Sedhat)
- Allows students to log hardware demand requests with status defaulting to `PENDING`.
- **Atomic Approval & Issuance:**
  1. Verifies demand exists and is `PENDING`.
  2. Confirms item available stock $\ge$ requested quantity.
  3. Deducts stock exactly once: $\text{Available} = \text{Available} - \text{Requested}$.
  4. Changes status to `ISSUED`.
  5. Records `STOCK_ISSUED` in stock history with student ID and demand ID.
- Rejecting a demand sets status to `REJECTED` and requires a justification reason; pending and rejected demands **never** alter stock.

### Module D: Stock History & Audit Trail (Anmol / Janvi)
- Tracks chronological inventory transactions: `STOCK_ADDED`, `STOCK_ISSUED`, `STOCK_ADJUSTED`, and `HISTORICAL_ENTRY`.
- Provides recent movement views (last 10 transactions) and filtering by Item ID.
- Recording historical past consumption records quantity change as $0$, preserving active stock integrity.

### Module E: Demand Prediction Engine (Harshita)
- Core algorithmic component. Computes explainable 3-month moving average consumption.
- Distinguishes **known zero demand** (e.g., recorded holiday month with 0 units) from **unknown missing coverage**.
- Marks predictions as `INSUFFICIENT_HISTORY` if any of the three prior completed calendar months lacks historical coverage.
- Calculates suggested restock quantities rounded up to the nearest whole unit.
- Flags items where predicted demand exceeds available inventory as `HIGH DEMAND`.

### Module F: Stock Alerts (Anmol)
- Consolidated warning feed categorizing items by:
  - Low stock warning.
  - Critical stockout alert.
  - High demand alert (predicted demand > current available stock).
  - Restocking recommendations with estimated procurement cost based on unit price.

### Module G: Executive Dashboard & Analytics (All Members)
- Real-time management console displaying:
  - Total items, active units, and capital inventory valuation.
  - Total registered students.
  - Pending, issued, and rejected demand counts.
  - Current-month issued units.
  - Real-time count of low-stock and out-of-stock SKUs.
  - High-demand risk items.
  - Recent stock movements.
- Generates tabular reports for inventory valuation, student directory, demand lifecycle, monthly consumption summary, and stock movement.

---

## 9. Prediction Algorithm & Historical Coverage Explained

### Mathematical Formulation
To forecast student demand for an inventory item for a target calendar month $T$:

1. Identify the previous three completed calendar months:
   $$M_1 = T - 3 \text{ months}, \quad M_2 = T - 2 \text{ months}, \quad M_3 = T - 1 \text{ month}$$
2. For each month $M_i$, compute total consumed units $Q_i$:
   $$Q_i = \sum \text{Issued Student Demands in } M_i + \text{Historical Recorded Demand in } M_i$$
3. If any month $M_i$ has neither an explicit historical record nor an issued demand, the month is marked as **lacking coverage**.
4. If all 3 months have complete coverage:
   $$\text{Predicted Demand} = \frac{Q_1 + Q_2 + Q_3}{3}$$
   $$\text{Suggested Restock Quantity} = \max\left(0, \lceil \text{Predicted Demand} + \text{Safety Stock} - \text{Available Stock} \rceil\right)$$
   *(where Safety Stock = item's `minimumStock` threshold)*

### Academic Test Case (Prompt Specification)
Consider an item with:
- Month 1 demand ($Q_1$) = 10 units
- Month 2 demand ($Q_2$) = 20 units
- Month 3 demand ($Q_3$) = 30 units
- Current available stock = 12 units
- Minimum stock (Safety stock) = 5 units

Calculation:
$$\text{Predicted Demand} = \frac{10 + 20 + 30}{3} = \frac{60}{3} = 20.0 \text{ units}$$
$$\text{Suggested Restock} = \max\left(0, \lceil 20.0 + 5 - 12 \rceil\right) = \max(0, 13) = 13 \text{ units}$$
$$\text{High Demand Flag} = 20.0 > 12 \implies \mathbf{TRUE}$$

### Handling Known Zero vs. Missing Data
- **Scenario A (Known Zero):** If the department was closed in September (vacation) and the administrator explicitly recorded 0 units consumed, September is covered with $Q = 0$. The 3-month average calculation proceeds normally.
- **Scenario B (Missing Data):** If no record exists for September, September has **no coverage**. StockWise marks the prediction as `INSUFFICIENT_HISTORY` and refuses to guess or silently assume 0 units.

---

## 10. Team Member Responsibilities & Git Branches

Each team member has dedicated ownership of specific components, matching the team branch layout:

```
                  +--------------------------------+
                  |         main (Stable)          |
                  +--------------------------------+
                    /          |           |         \
                   /           |           |          \
     harshita-prediction  janvi-database  anmol-inventory  sedhat-demand
```

### Member 1: Harshita
- **Branch:** `harshita-prediction`
- **Assigned Files:**
  - `src/com/stockwise/model/Prediction.java`
  - `src/com/stockwise/model/HistoricalDemand.java`
  - `src/com/stockwise/model/PredictionStatus.java`
  - `src/com/stockwise/repository/PredictionRepository.java`
  - `src/com/stockwise/repository/InMemoryPredictionRepository.java`
  - `src/com/stockwise/repository/HistoricalDemandRepository.java`
  - `src/com/stockwise/repository/InMemoryHistoricalDemandRepository.java`
  - `src/com/stockwise/service/PredictionService.java`
  - `test/com/stockwise/PredictionServiceTest.java`

### Member 2: Janvi
- **Branch:** `janvi-database`
- **Assigned Files:**
  - `src/com/stockwise/storage/AppContext.java`
  - `src/com/stockwise/repository/*` (Interface contracts coordination)
  - `src/com/stockwise/util/IDGenerator.java`
  - `src/com/stockwise/util/ValidationUtils.java`
  - `src/com/stockwise/util/DateUtils.java`
  - `PROJECT_CONTRACTS.md` & Evaluation 2 JDBC migration design

### Member 3: Anmol
- **Branch:** `anmol-inventory`
- **Assigned Files:**
  - `src/com/stockwise/model/Inventory.java`
  - `src/com/stockwise/model/StockStatus.java`
  - `src/com/stockwise/model/StockHistory.java`
  - `src/com/stockwise/model/TransactionType.java`
  - `src/com/stockwise/repository/InventoryRepository.java`
  - `src/com/stockwise/repository/InMemoryInventoryRepository.java`
  - `src/com/stockwise/repository/StockHistoryRepository.java`
  - `src/com/stockwise/repository/InMemoryStockHistoryRepository.java`
  - `src/com/stockwise/service/InventoryService.java`
  - `src/com/stockwise/service/StockAlertService.java`
  - `test/com/stockwise/InventoryServiceTest.java`
  - `test/com/stockwise/StockHistoryTest.java`

### Member 4: Sedhat
- **Branch:** `sedhat-demand`
- **Assigned Files:**
  - `src/com/stockwise/model/Student.java`
  - `src/com/stockwise/model/DemandRecord.java`
  - `src/com/stockwise/model/DemandStatus.java`
  - `src/com/stockwise/repository/StudentRepository.java`
  - `src/com/stockwise/repository/InMemoryStudentRepository.java`
  - `src/com/stockwise/repository/DemandRepository.java`
  - `src/com/stockwise/repository/InMemoryDemandRepository.java`
  - `src/com/stockwise/service/StudentService.java`
  - `src/com/stockwise/service/DemandService.java`
  - `test/com/stockwise/StudentServiceTest.java`
  - `test/com/stockwise/DemandServiceTest.java`

---

## 11. GitHub Collaboration Workflow

To maintain clean code and avoid merge conflicts, team members must adhere to this Git feature branch workflow:

1. **Pull Latest Main:**
   ```bash
   git checkout main
   git pull origin main
   ```
2. **Switch to Assigned Branch:**
   ```bash
   # Example for Harshita:
   git checkout harshita-prediction
   git merge main
   ```
3. **Make and Verify Changes:**
   Edit the assigned Java files, compile with `./compile.sh`, and run tests with `./test.sh`.
4. **Stage and Commit Clean Code:**
   ```bash
   git status
   git add src/com/stockwise/service/PredictionService.java
   git commit -m "feat(prediction): implement 3-month moving average with coverage check"
   ```
5. **Push Assigned Branch to GitHub:**
   ```bash
   git push origin harshita-prediction
   ```
6. **Open Pull Request (PR):**
   Navigate to the GitHub repository and open a Pull Request from `harshita-prediction` into `main`.
7. **Peer Review & Verification:**
   At least one team member reviews the PR, runs `./test.sh` to confirm 28/28 tests pass, and merges into `main`.

---

## 12. Compilation and Execution Instructions

### Prerequisites
- JDK 17 or newer installed. Check with:
  ```bash
  java -version
  javac -version
  ```

### Option A: Using Helper Scripts (Fastest & Recommended)
```bash
# 1. Compile the application and test suites
./compile.sh

# 2. Run the interactive console application
./run.sh

# 3. Run the automated test suite
./test.sh
```

### Option B: Using Standard JDK Commands Directly
```bash
# Compile everything into bin/ directory:
javac --release 17 -d bin $(find src test -name "*.java")

# Run the console application:
java -cp bin com.stockwise.Main

# Run the automated unit tests:
java -cp bin com.stockwise.TestRunner
```

---

## 13. Sample Console Walkthrough

When you start StockWise (`./run.sh`), the main menu appears:

```
====================================================
                     STOCKWISE
          SMART INVENTORY. SMARTER DECISIONS.
====================================================
                     MAIN MENU
----------------------------------------------------
  1. Inventory Management
  2. Student Management
  3. Demand Management
  4. Stock History
  5. Demand Prediction
  6. Stock Alerts
  7. Dashboard and Reports
  8. Exit
====================================================
Enter your choice (1-8):
```

### Example: Running a Demand Prediction
Selecting `5` (Demand Prediction) -> `1` (Predict Demand for an Item) -> Enter `ITM-1002` (Breadboard):

```
+---------------------------------------------------------------------------------+
| DEMAND PREDICTION & RESTOCKING REPORT                                            |
+---------------------------------------------------------------------------------+
| Item:                 Breadboard 830-Point (ITM-1002)                           |
| Target Forecast:      Dec 2026                                                  |
| Data Coverage Status: Sufficient History                                        |
+---------------------------------------------------------------------------------+
| 3-MONTH HISTORICAL BASELINE EVALUATION:                                         |
|   Month 1 (Sep 2026): 20 units [Historical Record]                              |
|   Month 2 (Oct 2026): 25 units [Historical Record]                              |
|   Month 3 (Nov 2026): 30 units [Historical Record]                              |
+---------------------------------------------------------------------------------+
| Predicted Demand:     25.00                                                     |
| Available Stock:      8                                                         |
| Safety Stock (Min):   15                                                        |
| High Demand Warning:  YES (Predicted Demand > Current Stock)                    |
| Suggested Restock:    32 units [MAX(0, ceil(Pred + Min - Stock))]               |
+---------------------------------------------------------------------------------+
| Algorithm Explanation:                                                          |
| SUFFICIENT HISTORY: Average of [Sep 2026: 20, Oct 2026: 25, Nov 2026: 30] =     |
| 25.00 units. Safety Stock: 15, Available: 8 -> Suggested Restock: 32 units.     |
+---------------------------------------------------------------------------------+
```

---

## 14. Automated Test Suite & Viva Verification

StockWise includes 28 automated unit tests built using pure Java 17 features without JUnit or third-party test libraries.

Run the test suite:
```bash
./test.sh
```

### Verified Test Output
```
====================================================
           STOCKWISE AUTOMATED TEST SUITE           
   Evaluation 1: Core Java 17 In-Memory Verification
====================================================

Running Suite: InventoryServiceTest
----------------------------------------------------
  [PASS] testPreventDeletionWithDependentDemands         
  [PASS] testAddItemSuccessfully                         
  [PASS] testDuplicateItemRejection                      
  [PASS] testUpdateItemDetails                           
  [PASS] testPreventNegativeQuantities                   
  [PASS] testStockStatusCalculation                      
  [PASS] testDeleteItem                                  

Running Suite: StudentServiceTest
----------------------------------------------------
  [PASS] testSafeDeletionPreventedWithDemands            
  [PASS] testAddStudentSuccessfully                      
  [PASS] testDuplicateRollNumberRejection                
  [PASS] testDuplicateStudentIdRejection                 
  [PASS] testInvalidEmailAndPhoneValidation              
  [PASS] testSearchStudents                              
  [PASS] testUpdateStudent                               

Running Suite: DemandServiceTest
----------------------------------------------------
  [PASS] testIssueDemandDeductsStockExactlyOnce          
  [PASS] testPreventInsufficientStockIssuance            
  [PASS] testSubmitDemandSuccessfully                    
  [PASS] testPreventDuplicateIssuance                    
  [PASS] testRejectDemandDoesNotAlterStock               

Running Suite: StockHistoryTest
----------------------------------------------------
  [PASS] testHistoricalDemandEntriesDoNotModifyCurrentStock
  [PASS] testStockAdditionsCreateHistory                 
  [PASS] testStockIssuanceCreatesHistory                 

Running Suite: PredictionServiceTest
----------------------------------------------------
  [PASS] testExactAcademicRequirementPrediction          
  [PASS] testExplicitZeroDemandMonthIsTreatedAsKnownZero 
  [PASS] testPendingAndRejectedDemandsAreExcluded        
  [PASS] testIssuedDemandsAreIncludedInPrediction        
  [PASS] testDefaultTargetIsNextMonth                    
  [PASS] testMissingHistoricalMonthCausesInsufficientHistory

====================================================
                   TEST RESULTS                     
====================================================
  Total Tests Executed: 28
  Passed:               28
  Failed:               0
  Execution Time:       61 ms
====================================================

ALL TESTS PASSED SUCCESSFULLY! (100% SUCCESS RATE)
```

---

## 15. Known Limitations (Evaluation 1)

1. **Volatile In-Memory Storage:** Records exist in RAM collections and reset when the process terminates.
2. **Single-User Console Interface:** The console terminal interface processes one administrator operation at a time.
3. **Fixed 3-Month Window:** The moving average algorithm evaluates strictly the previous 3 completed calendar months. Extended seasonal trend analysis (e.g. 12-month exponential smoothing) will be added in future iterations.

---

## 16. Future Scope (Evaluation 2 & Beyond)

1. **Database Integration (Evaluation 2):** Implement JDBC repositories connected to a MySQL 8 database, replacing `InMemory*Repository` with `Jdbc*Repository`.
2. **Role-Based Authentication (RBAC):** Separate Student and Administrator logins with hashed passwords.
3. **Web / Graphical Interface:** Introduce a clean JavaFX desktop UI or lightweight REST API with a web dashboard.
4. **Barcode / QR Code Scanning:** Integrate camera or handheld USB barcode scanners for rapid check-out of lab component boxes.
5. **Automated Purchase Orders:** Generate PDF purchase requisitions when suggested restock thresholds are breached.
