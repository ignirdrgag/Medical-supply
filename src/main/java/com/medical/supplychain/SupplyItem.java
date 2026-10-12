package com.medical.supplychain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SupplyItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String category;
    private final String unit;
    private final int minimumStock;
    private final List<StockLot> lots = new ArrayList<>();

    public SupplyItem(String name, String category, String unit, int minimumStock) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.minimumStock = minimumStock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public List<StockLot> getLots() {
        return lots;
    }
}
