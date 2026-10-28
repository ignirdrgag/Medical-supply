package com.medical.supplychain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class StockMovement {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int id;
    private final int itemId;
    private final String itemName;
    private final int quantity;
    private final String destination;
    private final LocalDateTime movedAt;
    private final Integer userId;
    private final String userName;

    public StockMovement(int id, int itemId, String itemName, int quantity, String destination,
                         LocalDateTime movedAt, Integer userId, String userName) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.destination = destination;
        this.movedAt = movedAt;
        this.userId = userId;
        this.userName = userName;
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

    public int getQuantity() {
        return quantity;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getMovedAt() {
        return movedAt;
    }

    public String getMovedAtDisplay() {
        return movedAt == null ? "" : movedAt.format(DATE_TIME_FORMAT);
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName == null ? "Systeme" : userName;
    }
}
