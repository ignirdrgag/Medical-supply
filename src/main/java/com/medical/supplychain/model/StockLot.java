package com.medical.supplychain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class StockLot {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int id;
    private final int itemId;
    private final String itemName;
    private final int supplierId;
    private final String supplierName;
    private final int quantity;
    private final LocalDate expirationDate;
    private final LocalDateTime receivedAt;

    public StockLot(int id, int itemId, String itemName, int supplierId, String supplierName,
                    int quantity, LocalDate expirationDate, LocalDateTime receivedAt) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.receivedAt = receivedAt;
    }

    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public String getExpirationDisplay() {
        return expirationDate == null ? "" : expirationDate.format(DATE_FORMAT);
    }

    public String getReceivedAtDisplay() {
        return receivedAt == null ? "" : receivedAt.format(DATE_TIME_FORMAT);
    }

    public long getDaysLeft() {
        if (expirationDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }

    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        long days = getDaysLeft();
        return !isExpired() && days <= 30;
    }
}
