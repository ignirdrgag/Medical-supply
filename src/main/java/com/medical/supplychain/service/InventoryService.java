package com.medical.supplychain.service;

import com.medical.supplychain.dao.ItemDao;
import com.medical.supplychain.dao.LotDao;
import com.medical.supplychain.dao.MovementDao;
import com.medical.supplychain.dao.OrderDao;
import com.medical.supplychain.dao.SupplierDao;
import com.medical.supplychain.dao.UserDao;
import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.PurchaseOrder;
import com.medical.supplychain.model.StockLot;
import com.medical.supplychain.model.StockMovement;
import com.medical.supplychain.model.Supplier;
import com.medical.supplychain.model.SupplyItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class InventoryService {
    private final ItemDao itemDao = new ItemDao();
    private final LotDao lotDao = new LotDao();
    private final SupplierDao supplierDao = new SupplierDao();
    private final OrderDao orderDao = new OrderDao();
    private final MovementDao movementDao = new MovementDao();
    private final UserDao userDao = new UserDao();

    public List<SupplyItem> getItems() throws SQLException {
        return itemDao.findAllWithStock();
    }

    public SupplyItem getItem(int itemId) throws SQLException {
        return itemDao.findByIdWithStock(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable."));
    }

    public List<Supplier> getSuppliers() throws SQLException {
        return supplierDao.findAll();
    }

    public List<PurchaseOrder> getOrders() throws SQLException {
        return orderDao.findAll();
    }

    public List<PurchaseOrder> getOrdersForSupplier(int supplierId) throws SQLException {
        return orderDao.findBySupplier(supplierId);
    }

    public List<StockMovement> getMovements(int limit) throws SQLException {
        return movementDao.findAll(limit);
    }

    public List<StockLot> getLotsForItem(int itemId) throws SQLException {
        return lotDao.findByItem(itemId);
    }

    public List<StockLot> getLotsForSupplier(int supplierId) throws SQLException {
        return lotDao.findBySupplier(supplierId);
    }

    public List<StockLot> getExpiringLots(int withinDays) throws SQLException {
        return lotDao.findExpiring(withinDays);
    }

    public Supplier getSupplier(int supplierId) throws SQLException {
        return supplierDao.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable."));
    }

    public List<SupplyItem> getLowStockItems() throws SQLException {
        return itemDao.findAllWithStock().stream().filter(SupplyItem::isLowStock).toList();
    }

    public void addItem(String name, String category, String unit, int minimumStock) throws SQLException {
        requireText(name, "Le nom de l'article");
        requireText(category, "La categorie");
        requireText(unit, "L'unite");
        if (minimumStock < 0) {
            throw new IllegalArgumentException("Le seuil minimum ne peut pas etre negatif.");
        }
        itemDao.create(name.trim(), category.trim(), unit.trim(), minimumStock);
    }

    public void updateItem(int id, String name, String category, String unit, int minimumStock)
            throws SQLException {
        requireText(name, "Le nom de l'article");
        requireText(category, "La categorie");
        requireText(unit, "L'unite");
        if (minimumStock < 0) {
            throw new IllegalArgumentException("Le seuil minimum ne peut pas etre negatif.");
        }
        if (!itemDao.update(id, name.trim(), category.trim(), unit.trim(), minimumStock)) {
            throw new IllegalArgumentException("Article introuvable.");
        }
    }

    public void deleteItem(int id) throws SQLException {
        if (!itemDao.delete(id)) {
            throw new IllegalArgumentException("Article introuvable.");
        }
    }

    public void addSupplier(String name, String contact, String email) throws SQLException {
        requireText(name, "Le nom du fournisseur");
        requireText(contact, "Le contact");
        requireText(email, "L'e-mail");
        supplierDao.create(name.trim(), contact.trim(), email.trim());
    }

    public void updateSupplier(int id, String name, String contact, String email) throws SQLException {
        requireText(name, "Le nom du fournisseur");
        requireText(contact, "Le contact");
        requireText(email, "L'e-mail");
        if (!supplierDao.update(id, name.trim(), contact.trim(), email.trim())) {
            throw new IllegalArgumentException("Fournisseur introuvable.");
        }
    }

    public void deleteSupplier(int id) throws SQLException {
        if (!supplierDao.delete(id)) {
            throw new IllegalArgumentException("Fournisseur introuvable.");
        }
    }

    public void updateSupplierContact(int supplierId, String contact, String email) throws SQLException {
        requireText(contact, "Le contact");
        requireText(email, "L'e-mail");
        supplierDao.updateContact(supplierId, contact.trim(), email.trim());
    }

    public void receiveStock(int itemId, int supplierId, int quantity, LocalDate expirationDate,
                             int userId) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantite recue doit etre superieure a zero.");
        }
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'expiration doit etre aujourd'hui ou plus tard.");
        }
        itemDao.findByIdWithStock(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable."));
        supplierDao.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable."));
        lotDao.insert(itemId, supplierId, quantity, expirationDate);
    }

    public void issueStock(int itemId, int quantity, String destination, int userId) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantite a sortir doit etre superieure a zero.");
        }
        requireText(destination, "La destination");

        try (Connection connection = Db.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<StockLot> lots = lotDao.findAvailableForUpdate(connection, itemId);
                int available = lots.stream().mapToInt(StockLot::getQuantity).sum();
                if (available < quantity) {
                    throw new IllegalArgumentException("Stock utilisable insuffisant : " + available
                            + " disponible(s), " + quantity + " demandee(s).");
                }

                int remaining = quantity;
                for (StockLot lot : lots) {
                    if (remaining == 0) {
                        break;
                    }
                    int taken = Math.min(remaining, lot.getQuantity());
                    if (taken == lot.getQuantity()) {
                        lotDao.delete(connection, lot.getId());
                    } else {
                        lotDao.updateQuantity(connection, lot.getId(), lot.getQuantity() - taken);
                    }
                    remaining -= taken;
                }

                movementDao.insert(connection, itemId, quantity, destination.trim(), userId);
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                rollback(connection);
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public int createOrder(int supplierId, int itemId, int quantity, int userId) throws SQLException {
        supplierDao.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable."));
        itemDao.findByIdWithStock(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable."));
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantite commandee doit etre superieure a zero.");
        }
        return orderDao.create(supplierId, itemId, quantity, userId);
    }

    public void receiveOrder(int orderId, LocalDate expirationDate, int userId) throws SQLException {
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'expiration doit etre aujourd'hui ou plus tard.");
        }

        try (Connection connection = Db.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PurchaseOrder order = orderDao.findHeaderForUpdate(connection, orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Commande introuvable."));
                if (!order.isPending()) {
                    throw new IllegalArgumentException("Cette commande a deja ete receptionnee.");
                }
                if (order.getLines().isEmpty()) {
                    throw new IllegalArgumentException("Cette commande ne contient aucun article.");
                }

                for (PurchaseOrder.OrderLine line : order.getLines()) {
                    lotDao.insert(connection, line.getItemId(), order.getSupplierId(),
                            line.getQuantity(), expirationDate);
                }

                if (!orderDao.markReceived(connection, orderId)) {
                    throw new IllegalArgumentException("La commande n'a pas pu etre marquee comme receptionnee.");
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                rollback(connection);
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public int countUsers() throws SQLException {
        return userDao.count();
    }

    public int countItems() throws SQLException {
        return itemDao.count();
    }

    public int countSuppliers() throws SQLException {
        return supplierDao.count();
    }

    public int countPendingOrders() throws SQLException {
        return orderDao.countPending();
    }

    public int countMovements() throws SQLException {
        return movementDao.count();
    }

    private static void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // La connexion sera fermee par le try-with-resources.
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " est obligatoire.");
        }
    }
}
