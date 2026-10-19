# MediSupply Code Guide

This document describes the ten main classes in the project, their roles, and the important parts of their code. All Java files are in the `com.medical.supplychain` package under `src/main/java/com/medical/supplychain/`.

## Overview

`Main` starts the application and loads the data. `DataStore` reads and saves it. `AppData` holds the in-memory collections. `SupplyChainService` enforces the business rules. `DesktopApp` displays the Swing interface. The other classes represent the business objects used by the application.

```text
Main → DataStore → AppData ↔ SupplyChainService ↔ business classes
                                      ↑
                                 DesktopApp
```

## 1. `AppData.java` — application data state

This class contains the lists that make up the application state: items (`SupplyItem`), suppliers (`Supplier`), orders (`PurchaseOrder`), and stock movements (`StockMovement`). It is serializable so it can be saved to a file.

Important code:

```java
public final class AppData implements Serializable {
    private final List<SupplyItem> items = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<PurchaseOrder> orders = new ArrayList<>();
    private final List<StockMovement> movements = new ArrayList<>();
}
```

The `getItems()`, `getSuppliers()`, `getOrders()`, and `getMovements()` accessors provide access to the collections used by the service.

## 2. `DataStore.java` — loading and saving data

`DataStore` associates the application with a file path. `exists()` checks whether the file exists, `load()` deserializes an `AppData` object, and `save()` serializes the data. Saving uses a temporary file and then attempts an atomic move, reducing the risk of leaving a partially written file.

```java
try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporaryFile))) {
    output.writeObject(data);
}
Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.ATOMIC_MOVE);
```

If the system does not support an atomic move, the code retries with a standard move. Input/output errors are passed to the caller as an `IOException`.

## 3. `DesktopApp.java` — graphical interface

This class is the main Swing window (`JFrame`). It builds the navigation and the Dashboard, Inventory, Suppliers, Orders, and Movements pages. Tables and buttons display data and trigger operations on `SupplyChainService`.

Example of adding a page:

```java
addPage("inventory", createInventoryPage());
```

The `CardLayout` displays one page at a time. Form operations are executed and then saved: the private `execute(...)` method runs the operation, calls `store.save(service.getData())`, and refreshes the display. The class also handles input and save errors through dialog boxes. `applyLookAndFeel()` configures the Swing appearance.

## 4. `Main.java` — entry point

`Main` contains `public static void main(String[] args)`, the Java entry point. The data file path can be passed as an argument; otherwise, the program uses `data/medical-supply-chain.ser`. It loads existing data or creates a demo dataset, then initializes the service and opens the window on Swing's event dispatch thread.

```java
Path dataFile = args.length > 0 ? Path.of(args[0])
        : Path.of("data", "medical-supply-chain.ser");
DataStore store = new DataStore(dataFile);
AppData data = store.exists() ? store.load() : createDemoData();
```

`createDemoData()` adds a supplier, several items, and initial stock lots. Loading errors are printed to the console and stop the program.

## 5. `PurchaseOrder.java` — supplier order

An order contains an ID, supplier, date, order lines, and a status. `OrderLine` associates an item ID with a quantity. The possible statuses are `ORDERED` and `RECEIVED`.

```java
public enum Status { ORDERED, RECEIVED }
...
public void markReceived() {
    status = Status.RECEIVED;
}
```

The constructor generates an ID, sets the current date, and starts the order with the `ORDERED` status. The actual receipt and addition of stock are handled by `SupplyChainService`.

## 6. `StockLot.java` — stock lot

A lot represents a quantity of an item with an expiration date and an originating supplier. It has a unique ID. Its quantity can be reduced when stock is issued.

```java
public void removeQuantity(int amount) {
    if (amount <= 0 || amount > quantity) {
        throw new IllegalArgumentException("Invalid lot quantity.");
    }
    quantity -= amount;
}
```

This validation prevents removing a zero, negative, or greater-than-available quantity.

## 7. `StockMovement.java` — issue history

This class records a stock issue: the item, quantity, destination, and date/time. The ID is generated when the object is created. `SupplyChainService.issueStock(...)` adds movements to the movement list.

```java
private final String itemId;
private final int quantity;
private final String destination;
private final LocalDateTime date;
```

A movement is a historical record: it describes a recorded issue, while the available quantity is updated in the lots.

## 8. `Supplier.java` — supplier

A supplier has a generated ID, name, contact details, and email address. Its fields are read-only after construction. Orders and lots refer to it by ID.

```java
public Supplier(String name, String contact, String email) {
    this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    this.name = name;
    this.contact = contact;
    this.email = email;
}
```

## 9. `SupplyChainService.java` — business rules

This is the layer that manages stock and supply operations. It receives an `AppData` object, validates input, creates items and suppliers, calculates stock levels, creates and receives orders, and records stock issues.

Stock issues follow the FEFO (*First Expired, First Out*) rule: lots with the nearest expiration date are used first. Expired lots are skipped, and an issue is rejected if usable stock is insufficient.

```java
lots.sort(Comparator.comparing(StockLot::getExpirationDate));
```

The service also provides lists of low-stock items and lots expiring within a given period. Validation failures throw `IllegalArgumentException`, for example when a quantity is invalid or an ID does not match an existing object.

## 10. `SupplyItem.java` — catalog item

An item describes a tracked product: ID, name, category, unit, minimum stock threshold, and list of lots. Its descriptive fields are set at creation; the lots hold the associated stock details.

```java
private final String id;
private final String name;
private final String category;
private final String unit;
private final int minimumStock;
private final List<StockLot> lots = new ArrayList<>();
```

Total and usable quantities are not stored on the item. The service calculates them from the lots, excluding expired lots from usable stock.

## Example operation flow

For example, when receiving an order, the interface asks the user for the required information and calls the service. The service validates the order and expiration dates, creates the corresponding lots, and changes the order status to `RECEIVED`. `DesktopApp` then saves the resulting state through `DataStore`.

The business data classes (`AppData`, `PurchaseOrder`, `StockLot`, `StockMovement`, `Supplier`, and `SupplyItem`) implement `Serializable` because they are persisted. `SupplyChainService` and `DesktopApp` handle, respectively, business rules and display, and are not serialized.
