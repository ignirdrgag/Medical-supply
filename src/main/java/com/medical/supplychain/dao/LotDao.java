package com.medical.supplychain.dao;

import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.StockLot;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class LotDao {
    private static final String SELECT_BASE = """
            SELECT l.id, l.item_id, l.supplier_id, l.quantity, l.expiration_date, l.received_at,
                   i.name AS item_name, s.name AS supplier_name
            FROM lots l
            JOIN items i ON i.id = l.item_id
            JOIN suppliers s ON s.id = l.supplier_id
            """;

    public List<StockLot> findByItem(int itemId) throws SQLException {
        String sql = SELECT_BASE + " WHERE l.item_id = ? ORDER BY l.expiration_date";
        List<StockLot> lots = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    lots.add(map(result));
                }
            }
        }
        return lots;
    }

    public List<StockLot> findBySupplier(int supplierId) throws SQLException {
        String sql = SELECT_BASE + " WHERE l.supplier_id = ? ORDER BY l.expiration_date";
        List<StockLot> lots = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, supplierId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    lots.add(map(result));
                }
            }
        }
        return lots;
    }

    public List<StockLot> findExpiring(int withinDays) throws SQLException {
        String sql = SELECT_BASE + """
                 WHERE l.quantity > 0
                   AND l.expiration_date >= CURDATE()
                   AND l.expiration_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)
                 ORDER BY l.expiration_date
                """;
        List<StockLot> lots = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, withinDays);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    lots.add(map(result));
                }
            }
        }
        return lots;
    }

    public void insert(int itemId, int supplierId, int quantity, LocalDate expirationDate)
            throws SQLException {
        try (Connection connection = Db.getConnection()) {
            insert(connection, itemId, supplierId, quantity, expirationDate);
        }
    }

    public void insert(Connection connection, int itemId, int supplierId, int quantity,
                       LocalDate expirationDate) throws SQLException {
        String sql = """
                INSERT INTO lots (item_id, supplier_id, quantity, expiration_date)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);
            statement.setInt(2, supplierId);
            statement.setInt(3, quantity);
            statement.setDate(4, Date.valueOf(expirationDate));
            statement.executeUpdate();
        }
    }

    public List<StockLot> findAvailableForUpdate(Connection connection, int itemId) throws SQLException {
        String sql = """
                SELECT l.id, l.item_id, l.supplier_id, l.quantity, l.expiration_date, l.received_at,
                       i.name AS item_name, s.name AS supplier_name
                FROM lots l
                JOIN items i ON i.id = l.item_id
                JOIN suppliers s ON s.id = l.supplier_id
                WHERE l.item_id = ? AND l.quantity > 0 AND l.expiration_date >= CURDATE()
                ORDER BY l.expiration_date, l.id
                FOR UPDATE
                """;
        List<StockLot> lots = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    lots.add(map(result));
                }
            }
        }
        return lots;
    }

    public void updateQuantity(Connection connection, int lotId, int quantity) throws SQLException {
        String sql = "UPDATE lots SET quantity = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, lotId);
            statement.executeUpdate();
        }
    }

    public void delete(Connection connection, int lotId) throws SQLException {
        String sql = "DELETE FROM lots WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, lotId);
            statement.executeUpdate();
        }
    }

    private StockLot map(ResultSet result) throws SQLException {
        Timestamp receivedAt = result.getTimestamp("received_at");
        return new StockLot(
                result.getInt("id"),
                result.getInt("item_id"),
                result.getString("item_name"),
                result.getInt("supplier_id"),
                result.getString("supplier_name"),
                result.getInt("quantity"),
                result.getDate("expiration_date").toLocalDate(),
                receivedAt == null ? null : receivedAt.toLocalDateTime());
    }
}
