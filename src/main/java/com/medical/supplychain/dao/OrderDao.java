package com.medical.supplychain.dao;

import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.PurchaseOrder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class OrderDao {
    private static final String SELECT_HEADER = """
            SELECT o.id, o.supplier_id, o.order_date, o.status, o.created_by,
                   s.name AS supplier_name, u.full_name AS created_by_name
            FROM purchase_orders o
            JOIN suppliers s ON s.id = o.supplier_id
            LEFT JOIN users u ON u.id = o.created_by
            """;

    public List<PurchaseOrder> findAll() throws SQLException {
        try (Connection connection = Db.getConnection()) {
            return findOrders(connection, SELECT_HEADER + " ORDER BY o.order_date DESC, o.id DESC", null);
        }
    }

    public List<PurchaseOrder> findBySupplier(int supplierId) throws SQLException {
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SELECT_HEADER + " WHERE o.supplier_id = ? ORDER BY o.order_date DESC, o.id DESC")) {
            statement.setInt(1, supplierId);
            try (ResultSet result = statement.executeQuery()) {
                return collectOrders(connection, result);
            }
        }
    }

    public Optional<PurchaseOrder> findById(int id) throws SQLException {
        try (Connection connection = Db.getConnection()) {
            return findHeader(connection, id, false);
        }
    }

    public Optional<PurchaseOrder> findHeaderForUpdate(Connection connection, int id) throws SQLException {
        return findHeader(connection, id, true);
    }

    public List<PurchaseOrder.OrderLine> findLines(Connection connection, int orderId) throws SQLException {
        String sql = """
                SELECT ol.id, ol.item_id, ol.quantity, i.name AS item_name
                FROM purchase_order_lines ol
                JOIN items i ON i.id = ol.item_id
                WHERE ol.order_id = ?
                ORDER BY ol.id
                """;
        List<PurchaseOrder.OrderLine> lines = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    lines.add(new PurchaseOrder.OrderLine(
                            result.getInt("id"),
                            result.getInt("item_id"),
                            result.getString("item_name"),
                            result.getInt("quantity")));
                }
            }
        }
        return lines;
    }

    public int create(int supplierId, int itemId, int quantity, int userId) throws SQLException {
        String orderSql = """
                INSERT INTO purchase_orders (supplier_id, order_date, status, created_by)
                VALUES (?, CURDATE(), 'ORDERED', ?)
                """;
        String lineSql = """
                INSERT INTO purchase_order_lines (order_id, item_id, quantity) VALUES (?, ?, ?)
                """;
        try (Connection connection = Db.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement order = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement line = connection.prepareStatement(lineSql)) {
                order.setInt(1, supplierId);
                order.setInt(2, userId);
                order.executeUpdate();
                int orderId;
                try (ResultSet keys = order.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
                line.setInt(1, orderId);
                line.setInt(2, itemId);
                line.setInt(3, quantity);
                line.executeUpdate();
                connection.commit();
                return orderId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean markReceived(Connection connection, int orderId) throws SQLException {
        String sql = "UPDATE purchase_orders SET status = 'RECEIVED' WHERE id = ? AND status = 'ORDERED'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            return statement.executeUpdate() > 0;
        }
    }

    public int countPending() throws SQLException {
        String sql = "SELECT COUNT(*) FROM purchase_orders WHERE status = 'ORDERED'";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    private List<PurchaseOrder> findOrders(Connection connection, String sql, Integer supplierId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (supplierId != null) {
                statement.setInt(1, supplierId);
            }
            try (ResultSet result = statement.executeQuery()) {
                return collectOrders(connection, result);
            }
        }
    }

    private List<PurchaseOrder> collectOrders(Connection connection, ResultSet result) throws SQLException {
        List<Header> headers = new ArrayList<>();
        while (result.next()) {
            headers.add(readHeader(result));
        }
        List<PurchaseOrder> orders = new ArrayList<>();
        for (Header header : headers) {
            orders.add(header.toOrder(findLines(connection, header.id())));
        }
        return orders;
    }

    private Optional<PurchaseOrder> findHeader(Connection connection, int id, boolean forUpdate)
            throws SQLException {
        String sql = forUpdate
                ? """
                  SELECT o.id, o.supplier_id, o.order_date, o.status, o.created_by,
                         s.name AS supplier_name, NULL AS created_by_name
                  FROM purchase_orders o
                  JOIN suppliers s ON s.id = o.supplier_id
                  WHERE o.id = ?
                  FOR UPDATE
                  """
                : SELECT_HEADER + " WHERE o.id = ?";
        Header header;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                header = readHeader(result);
            }
        }
        return Optional.of(header.toOrder(findLines(connection, header.id())));
    }

    private Header readHeader(ResultSet result) throws SQLException {
        Date orderDate = result.getDate("order_date");
        int createdBy = result.getInt("created_by");
        Integer creator = result.wasNull() ? null : createdBy;
        return new Header(
                result.getInt("id"),
                result.getInt("supplier_id"),
                result.getString("supplier_name"),
                orderDate == null ? LocalDate.now() : orderDate.toLocalDate(),
                PurchaseOrder.Status.valueOf(result.getString("status")),
                creator,
                result.getString("created_by_name"));
    }

    private record Header(int id, int supplierId, String supplierName, LocalDate orderDate,
                          PurchaseOrder.Status status, Integer createdBy, String createdByName) {
        private PurchaseOrder toOrder(List<PurchaseOrder.OrderLine> lines) {
            return new PurchaseOrder(id, supplierId, supplierName, orderDate, status,
                    createdBy, createdByName, lines);
        }
    }
}
