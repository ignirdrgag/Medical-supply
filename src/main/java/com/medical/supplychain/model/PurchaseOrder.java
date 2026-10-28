package com.medical.supplychain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class PurchaseOrder {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public enum Status {
        ORDERED("En attente"),
        RECEIVED("Receptionnee");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static final class OrderLine {
        private final int id;
        private final int itemId;
        private final String itemName;
        private final int quantity;

        public OrderLine(int id, int itemId, String itemName, int quantity) {
            this.id = id;
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
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
    }

    private final int id;
    private final int supplierId;
    private final String supplierName;
    private final LocalDate orderDate;
    private final Status status;
    private final Integer createdBy;
    private final String createdByName;
    private final List<OrderLine> lines;

    public PurchaseOrder(int id, int supplierId, String supplierName, LocalDate orderDate,
                         Status status, Integer createdBy, String createdByName, List<OrderLine> lines) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.orderDate = orderDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.lines = lines;
    }

    public int getId() {
        return id;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getOrderDateDisplay() {
        return orderDate == null ? "" : orderDate.format(DATE_FORMAT);
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return status.getLabel();
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public int getTotalQuantity() {
        return lines.stream().mapToInt(OrderLine::getQuantity).sum();
    }

    public boolean isPending() {
        return status == Status.ORDERED;
    }
}
