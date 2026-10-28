package com.medical.supplychain.dao;

import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SupplierDao {
    public List<Supplier> findAll() throws SQLException {
        String sql = "SELECT id, name, contact, email FROM suppliers ORDER BY name";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                suppliers.add(map(result));
            }
        }
        return suppliers;
    }

    public Optional<Supplier> findById(int id) throws SQLException {
        String sql = "SELECT id, name, contact, email FROM suppliers WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public void create(String name, String contact, String email) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact, email) VALUES (?, ?, ?)";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, contact);
            statement.setString(3, email);
            statement.executeUpdate();
        }
    }

    public void updateContact(int id, String contact, String email) throws SQLException {
        String sql = "UPDATE suppliers SET contact = ?, email = ? WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, contact);
            statement.setString(2, email);
            statement.setInt(3, id);
            statement.executeUpdate();
        }
    }

    public boolean update(int id, String name, String contact, String email) throws SQLException {
        String sql = "UPDATE suppliers SET name = ?, contact = ?, email = ? WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, contact);
            statement.setString(3, email);
            statement.setInt(4, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM suppliers";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    private Supplier map(ResultSet result) throws SQLException {
        return new Supplier(
                result.getInt("id"),
                result.getString("name"),
                result.getString("contact"),
                result.getString("email"));
    }
}
