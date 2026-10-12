package com.medical.supplychain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public final class StockMovement implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String itemId;
    private final int quantity;
    private final String destination;
    private final LocalDateTime date;

    public StockMovement(String itemId, int quantity, String destination) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.itemId = itemId;
        this.quantity = quantity;
        this.destination = destination;
        this.date = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
