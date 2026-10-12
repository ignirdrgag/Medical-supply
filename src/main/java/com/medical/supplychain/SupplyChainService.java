package com.medical.supplychain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SupplyChainService {
    private final AppData data;

    public SupplyChainService(AppData data) {
        this.data = data;
    }

    public AppData getData() {
        return data;
    }

    public List<SupplyItem> getItems() {
        return data.getItems().stream()
                .sorted(Comparator.comparing(SupplyItem::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Supplier> getSuppliers() {
        return List.copyOf(data.getSuppliers());
    }

    public List<PurchaseOrder> getOrders() {
        return data.getOrders().stream()
                .sorted(Comparator.comparing(PurchaseOrder::getOrderDate).reversed())
                .toList();
    }

    public List<StockMovement> getMovements() {
        return data.getMovements().stream()
                .sorted(Comparator.comparing(StockMovement::getDate).reversed())
                .toList();
    }

    public SupplyItem addItem(String name, String category, String unit, int minimumStock) {
        requireText(name, "Le nom de l'article");
        requireText(category, "La catégorie");
        requireText(unit, "L'unité");
        if (minimumStock < 0) {
            throw new IllegalArgumentException("Le seuil de stock ne peut pas être négatif.");
        }
        SupplyItem item = new SupplyItem(name.trim(), category.trim(), unit.trim(), minimumStock);
        data.getItems().add(item);
        return item;
    }

    public Supplier addSupplier(String name, String contact, String email) {
        requireText(name, "Le nom du fournisseur");
        requireText(contact, "Le contact");
        requireText(email, "L'adresse e-mail");
        Supplier supplier = new Supplier(name.trim(), contact.trim(), email.trim());
        data.getSuppliers().add(supplier);
        return supplier;
    }

    public int getQuantity(SupplyItem item) {
        return item.getLots().stream().mapToInt(StockLot::getQuantity).sum();
    }

    public int getUsableQuantity(SupplyItem item) {
        LocalDate today = LocalDate.now();
        return item.getLots().stream()
                .filter(lot -> !lot.getExpirationDate().isBefore(today))
                .mapToInt(StockLot::getQuantity)
                .sum();
    }

    public void receiveStock(String itemId, int quantity, LocalDate expirationDate, String supplierId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité reçue doit être supérieure à zéro.");
        }
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'expiration doit être aujourd'hui ou ultérieure.");
        }
        SupplyItem item = findItem(itemId);
        findSupplier(supplierId);
        item.getLots().add(new StockLot(quantity, expirationDate, supplierId));
    }

    public void issueStock(String itemId, int quantity, String destination) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité à sortir doit être supérieure à zéro.");
        }
        requireText(destination, "La destination");
        SupplyItem item = findItem(itemId);
        int available = getUsableQuantity(item);
        if (available < quantity) {
            throw new IllegalArgumentException("Stock utilisable insuffisant : " + available + " "
                    + item.getUnit() + " disponible(s).");
        }

        int remaining = quantity;
        List<StockLot> lots = item.getLots();
        lots.sort(Comparator.comparing(StockLot::getExpirationDate));
        for (int i = 0; i < lots.size() && remaining > 0;) {
            StockLot lot = lots.get(i);
            if (lot.getExpirationDate().isBefore(LocalDate.now())) {
                i++;
                continue;
            }
            int issued = Math.min(remaining, lot.getQuantity());
            remaining -= issued;
            if (issued == lot.getQuantity()) {
                lots.remove(i);
            } else {
                lot.removeQuantity(issued);
                i++;
            }
        }
        data.getMovements().add(new StockMovement(item.getId(), quantity, destination.trim()));
    }

    public PurchaseOrder createOrder(String supplierId, String itemId, int quantity) {
        findSupplier(supplierId);
        findItem(itemId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité commandée doit être supérieure à zéro.");
        }
        PurchaseOrder order = new PurchaseOrder(supplierId,
                List.of(new PurchaseOrder.OrderLine(itemId, quantity)));
        data.getOrders().add(order);
        return order;
    }

    public void receiveOrder(String orderId, Map<String, LocalDate> expirationDates) {
        PurchaseOrder order = findOrder(orderId);
        if (order.getStatus() != PurchaseOrder.Status.ORDERED) {
            throw new IllegalArgumentException("Cette commande a déjà été réceptionnée.");
        }
        for (PurchaseOrder.OrderLine line : order.getLines()) {
            LocalDate expirationDate = expirationDates.get(line.getItemId());
            if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Chaque article doit avoir une date d'expiration valide.");
            }
        }
        for (PurchaseOrder.OrderLine line : order.getLines()) {
            receiveStock(line.getItemId(), line.getQuantity(), expirationDates.get(line.getItemId()),
                    order.getSupplierId());
        }
        order.markReceived();
    }

    public List<SupplyItem> getLowStockItems() {
        return getItems().stream()
                .filter(item -> getUsableQuantity(item) <= item.getMinimumStock())
                .toList();
    }

    public List<StockLot> getExpiringLots(int withinDays) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(withinDays);
        List<StockLot> result = new ArrayList<>();
        for (SupplyItem item : data.getItems()) {
            result.addAll(getExpiringLots(item, withinDays));
        }
        return result;
    }

    public List<StockLot> getExpiringLots(SupplyItem item, int withinDays) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(withinDays);
        return item.getLots().stream()
                .filter(lot -> !lot.getExpirationDate().isBefore(today)
                        && !lot.getExpirationDate().isAfter(limit))
                .toList();
    }

    public Optional<SupplyItem> findItemOptional(String id) {
        return data.getItems().stream().filter(item -> item.getId().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<Supplier> findSupplierOptional(String id) {
        return data.getSuppliers().stream().filter(supplier -> supplier.getId().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<PurchaseOrder> findOrderOptional(String id) {
        return data.getOrders().stream().filter(order -> order.getId().equalsIgnoreCase(id)).findFirst();
    }

    public String getItemName(String id) {
        return findItem(id).getName();
    }

    public String getSupplierName(String id) {
        return findSupplier(id).getName();
    }

    private SupplyItem findItem(String id) {
        return findItemOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + id));
    }

    private Supplier findSupplier(String id) {
        return findSupplierOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable : " + id));
    }

    private PurchaseOrder findOrder(String id) {
        return findOrderOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + id));
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " est obligatoire.");
        }
    }
}
