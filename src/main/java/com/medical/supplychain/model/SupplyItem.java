package com.medical.supplychain.model;

public final class SupplyItem {
    private final int id;
    private final String name;
    private final String category;
    private final String unit;
    private final int minimumStock;
    private final int totalQuantity;
    private final int usableQuantity;

    public SupplyItem(int id, String name, String category, String unit, int minimumStock,
                      int totalQuantity, int usableQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.minimumStock = minimumStock;
        this.totalQuantity = totalQuantity;
        this.usableQuantity = usableQuantity;
    }

    public int getId() {
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

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getUsableQuantity() {
        return usableQuantity;
    }

    public int getExpiredQuantity() {
        return Math.max(0, totalQuantity - usableQuantity);
    }

    public boolean isLowStock() {
        return usableQuantity <= minimumStock;
    }

    public boolean isOutOfStock() {
        return usableQuantity == 0;
    }
}
