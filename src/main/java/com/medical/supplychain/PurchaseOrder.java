package com.medical.supplychain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PurchaseOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ORDERED,
        RECEIVED
    }

    public static final class OrderLine implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String itemId;
        private final int quantity;

        public OrderLine(String itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public String getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    private final String id;
    private final String supplierId;
    private final LocalDate orderDate;
    private final List<OrderLine> lines;
    private Status status;

    public PurchaseOrder(String supplierId, List<OrderLine> lines) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.supplierId = supplierId;
        this.orderDate = LocalDate.now();
        this.lines = new ArrayList<>(lines);
        this.status = Status.ORDERED;
    }

    public String getId() {
        return id;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public Status getStatus() {
        return status;
    }

    public void markReceived() {
        status = Status.RECEIVED;
    }
}
