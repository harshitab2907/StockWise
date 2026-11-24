package com.stockwise;

import com.stockwise.exception.DuplicateResourceException;
import com.stockwise.exception.ValidationException;
import com.stockwise.model.DemandRecord;
import com.stockwise.model.Inventory;
import com.stockwise.model.StockStatus;
import com.stockwise.repository.InMemoryDemandRepository;
import com.stockwise.repository.InMemoryInventoryRepository;
import com.stockwise.repository.InMemoryStockHistoryRepository;
import com.stockwise.service.InventoryService;

import static com.stockwise.TestAssertions.*;

/**
 * Unit tests verifying Module A: Inventory Management.
 */
public class InventoryServiceTest {

    private InMemoryInventoryRepository inventoryRepo;
    private InMemoryStockHistoryRepository stockHistoryRepo;
    private InMemoryDemandRepository demandRepo;
    private InventoryService service;

    public void setUp() {
        inventoryRepo = new InMemoryInventoryRepository();
        stockHistoryRepo = new InMemoryStockHistoryRepository();
        demandRepo = new InMemoryDemandRepository();
        service = new InventoryService(inventoryRepo, stockHistoryRepo, demandRepo);
    }

    public void testAddItemSuccessfully() {
        setUp();
        Inventory item = service.addItem("ITM-TEST1", "Microcontroller", "Lab", "Test desc", 50, 450.0, 10);
        assertNotNull(item, "Added item should not be null");
        assertEquals("ITM-TEST1", item.getItemId(), "Item ID matches");
        assertEquals(50, item.getAvailableQuantity(), "Available quantity matches");
        assertEquals(StockStatus.IN_STOCK, item.getStockStatus(), "Status should be IN_STOCK");
        assertEquals(1, inventoryRepo.count(), "Repository should contain 1 item");
        assertEquals(1, stockHistoryRepo.count(), "Stock history should record initial stock");
    }

    public void testDuplicateItemRejection() {
        setUp();
        service.addItem("ITM-DUP", "First Item", "Lab", "Desc", 10, 100.0, 5);
        assertThrows(DuplicateResourceException.class, () -> {
            service.addItem("ITM-DUP", "Second Item", "Lab", "Desc", 20, 200.0, 5);
        }, "Adding an item with existing ID must throw DuplicateResourceException");
    }

    public void testUpdateItemDetails() {
        setUp();
        service.addItem("ITM-UPD", "Original Name", "Electronics", "Old Desc", 20, 150.0, 5);
        Inventory updated = service.updateItem("ITM-UPD", "New Name", "Sensors", "New Desc", 175.0, 8);

        assertEquals("New Name", updated.getItemName(), "Name updated");
        assertEquals("Sensors", updated.getCategory(), "Category updated");
        assertEquals("New Desc", updated.getDescription(), "Description updated");
        assertEquals(175.0, updated.getPrice(), 0.001, "Price updated");
        assertEquals(8, updated.getMinimumStock(), "Min stock updated");
    }

    public void testDeleteItem() {
        setUp();
        service.addItem("ITM-DEL", "To Delete", "Tools", "", 10, 50.0, 2);
        assertEquals(1, inventoryRepo.count(), "Item is present");

        boolean deleted = service.deleteItem("ITM-DEL");
        assertTrue(deleted, "Item deletion should return true");
        assertEquals(0, inventoryRepo.count(), "Repository should be empty after deletion");
    }

    public void testPreventNegativeQuantities() {
        setUp();
        assertThrows(ValidationException.class, () -> {
            service.addItem("ITM-NEG", "Negative Item", "Lab", "", -5, 100.0, 2);
        }, "Negative initial quantity must throw ValidationException");

        Inventory item = service.addItem("ITM-VALID", "Valid Item", "Lab", "", 10, 100.0, 5);
        assertThrows(ValidationException.class, () -> {
            item.setAvailableQuantity(-10);
        }, "Setting negative quantity on model must throw ValidationException");
    }

    public void testStockStatusCalculation() {
        setUp();
        // IN_STOCK: qty > minStock
        Inventory inStock = service.addItem("ITM-IN", "In Stock Item", "Lab", "", 20, 10.0, 10);
        assertEquals(StockStatus.IN_STOCK, inStock.getStockStatus(), "Qty 20 > Min 10 is IN_STOCK");

        // LOW_STOCK: 0 < qty <= minStock
        Inventory lowStock = service.addItem("ITM-LOW", "Low Stock Item", "Lab", "", 10, 10.0, 10);
        assertEquals(StockStatus.LOW_STOCK, lowStock.getStockStatus(), "Qty 10 == Min 10 is LOW_STOCK");

        Inventory lowStock2 = service.addItem("ITM-LOW2", "Low Stock Item 2", "Lab", "", 1, 10.0, 10);
        assertEquals(StockStatus.LOW_STOCK, lowStock2.getStockStatus(), "Qty 1 <= Min 10 is LOW_STOCK");

        // OUT_OF_STOCK: qty == 0
        Inventory outOfStock = service.addItem("ITM-OUT", "Out of Stock Item", "Lab", "", 0, 10.0, 10);
        assertEquals(StockStatus.OUT_OF_STOCK, outOfStock.getStockStatus(), "Qty 0 is OUT_OF_STOCK");
    }

    public void testPreventDeletionWithDependentDemands() {
        setUp();
        service.addItem("ITM-DEP", "Item With Demands", "Lab", "", 20, 100.0, 5);
        demandRepo.save(new DemandRecord("DMD-1", "STU-1", "ITM-DEP", 2));

        assertThrows(ValidationException.class, () -> {
            service.deleteItem("ITM-DEP");
        }, "Deleting item with dependent demands must be rejected");
    }
}
