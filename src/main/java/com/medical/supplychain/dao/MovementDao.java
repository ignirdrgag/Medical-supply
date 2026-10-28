package com.medical.supplychain.dao;

import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.StockMovement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class MovementDao {
    private static final String SELECT_BASE = """
            SELECT m.id, m.item_id, m.quantity, m.destination, m.moved_at, m.user_id,
                   i.name AS item_name, u.full_name AS user_name
            FROM stock_movements m
            JOIN items i ON i.id = m.item_id
            LEFT JOIN users u ON u.id = m.user_id
            """;

    public List<StockMovement> findAll(int limit) throws SQLException {
        String sql = SELECT_BASE + " ORDER BY m.moved_at DESC, m.id DESC LIMIT ?";
        List<StockMovement> movements = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    movements.add(map(result));
                }
            }
        }
        return movements;
    }

    public void insert(Connection connection, int itemId, int quantity, String destination, int userId)
            throws SQLException {
        String sql = """
                INSERT INTO stock_movements (item_id, quantity, destination, user_id)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);
            statement.setInt(2, quantity);
            statement.setString(3, destination);
            statement.setInt(4, userId);
            statement.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM stock_movements";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    private StockMovement map(ResultSet result) throws SQLException {
        Timestamp movedAt = result.getTimestamp("moved_at");
        int userId = result.getInt("user_id");
        Integer user = result.wasNull() ? null : userId;
        return new StockMovement(
                result.getInt("id"),
                result.getInt("item_id"),
                result.getString("item_name"),
                result.getInt("quantity"),
                result.getString("destination"),
                movedAt == null ? null : movedAt.toLocalDateTime(),
                user,
                result.getString("user_name"));
    }
}
