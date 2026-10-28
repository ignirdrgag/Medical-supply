package com.medical.supplychain.dao;

import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.SupplyItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ItemDao {
    private static final String SELECT_WITH_STOCK = """
            SELECT i.id, i.name, i.category, i.unit, i.minimum_stock,
                   COALESCE(SUM(l.quantity), 0) AS total_quantity,
                   COALESCE(SUM(CASE WHEN l.expiration_date >= CURDATE() THEN l.quantity ELSE 0 END), 0)
                       AS usable_quantity
            FROM items i
            LEFT JOIN lots l ON l.item_id = i.id
            """;

    public List<SupplyItem> findAllWithStock() throws SQLException {
        String sql = SELECT_WITH_STOCK
                + " GROUP BY i.id, i.name, i.category, i.unit, i.minimum_stock ORDER BY i.name";
        List<SupplyItem> items = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                items.add(map(result));
            }
        }
        return items;
    }

    public Optional<SupplyItem> findByIdWithStock(int id) throws SQLException {
        String sql = SELECT_WITH_STOCK
                + " WHERE i.id = ? GROUP BY i.id, i.name, i.category, i.unit, i.minimum_stock";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public void create(String name, String category, String unit, int minimumStock) throws SQLException {
        String sql = "INSERT INTO items (name, category, unit, minimum_stock) VALUES (?, ?, ?, ?)";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, category);
            statement.setString(3, unit);
            statement.setInt(4, minimumStock);
            statement.executeUpdate();
        }
    }

    public boolean update(int id, String name, String category, String unit, int minimumStock) throws SQLException {
        String sql = "UPDATE items SET name = ?, category = ?, unit = ?, minimum_stock = ? WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, category);
            statement.setString(3, unit);
            statement.setInt(4, minimumStock);
            statement.setInt(5, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM items";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    private SupplyItem map(ResultSet result) throws SQLException {
        return new SupplyItem(
                result.getInt("id"),
                result.getString("name"),
                result.getString("category"),
                result.getString("unit"),
                result.getInt("minimum_stock"),
                result.getInt("total_quantity"),
                result.getInt("usable_quantity"));
    }
}
