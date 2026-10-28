package com.medical.supplychain.dao;

import com.medical.supplychain.db.Db;
import com.medical.supplychain.model.Role;
import com.medical.supplychain.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserDao {
    private static final String SELECT_BASE = """
            SELECT u.id, u.username, u.password, u.full_name, u.role, u.supplier_id,
                   s.name AS supplier_name
            FROM users u
            LEFT JOIN suppliers s ON s.id = u.supplier_id
            """;

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = SELECT_BASE + " WHERE u.username = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public Optional<User> findById(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE u.id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = SELECT_BASE + " ORDER BY u.role, u.full_name";
        List<User> users = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                users.add(map(result));
            }
        }
        return users;
    }

    public void create(String username, String password, String fullName, Role role,
                       Integer supplierId) throws SQLException {
        String sql = """
                INSERT INTO users (username, password, full_name, role, supplier_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, fullName);
            statement.setString(4, role.name());
            if (supplierId == null) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, supplierId);
            }
            statement.executeUpdate();
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(int id, String username, String password, String fullName,
                          Role role, Integer supplierId) throws SQLException {
        String sql = password == null
                ? "UPDATE users SET username = ?, full_name = ?, role = ?, supplier_id = ? WHERE id = ?"
                : "UPDATE users SET username = ?, password = ?, full_name = ?, role = ?, supplier_id = ? WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            statement.setString(index++, username);
            if (password != null) {
                statement.setString(index++, password);
            }
            statement.setString(index++, fullName);
            statement.setString(index++, role.name());
            if (supplierId == null) {
                statement.setNull(index++, Types.INTEGER);
            } else {
                statement.setInt(index++, supplierId);
            }
            statement.setInt(index, id);
            return statement.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    private User map(ResultSet result) throws SQLException {
        int supplierId = result.getInt("supplier_id");
        Integer supplier = result.wasNull() ? null : supplierId;
        return new User(
                result.getInt("id"),
                result.getString("username"),
                result.getString("password"),
                result.getString("full_name"),
                Role.valueOf(result.getString("role")),
                supplier,
                result.getString("supplier_name"));
    }
}
