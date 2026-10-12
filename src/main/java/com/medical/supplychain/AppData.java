package com.medical.supplychain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class AppData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<SupplyItem> items = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<PurchaseOrder> orders = new ArrayList<>();
    private final List<StockMovement> movements = new ArrayList<>();

    public List<SupplyItem> getItems() {
        return items;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public List<PurchaseOrder> getOrders() {
        return orders;
    }

    public List<StockMovement> getMovements() {
        return movements;
    }
}
