package com.medical.supplychain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public final class StockLot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private int quantity;
    private final LocalDate expirationDate;
    private final String supplierId;

    public StockLot(int quantity, LocalDate expirationDate, String supplierId) {
        this.id = UUID.randomUUID().toString();
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
    }

    public String getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void removeQuantity(int amount) {
        if (amount <= 0 || amount > quantity) {
            throw new IllegalArgumentException("Quantité de lot invalide.");
        }
        quantity -= amount;
    }
}
