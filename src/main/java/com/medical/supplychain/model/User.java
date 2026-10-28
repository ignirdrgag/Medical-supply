package com.medical.supplychain.model;

public final class User {
    private final int id;
    private final String username;
    private final String password;
    private final String fullName;
    private final Role role;
    private final Integer supplierId;
    private final String supplierName;

    public User(int id, String username, String password, String fullName, Role role,
                Integer supplierId, String supplierName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleLabel() {
        return role.getLabel();
    }

    public String getRoleName() {
        return role.name();
    }

    public String getRoleCssClass() {
        return role.getCssClass();
    }

    public String getHomePath() {
        return role.getHomePath();
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isStaff() {
        return role == Role.STAFF;
    }

    public boolean isManager() {
        return role == Role.MANAGER;
    }

    public boolean isDoctor() {
        return role == Role.DOCTOR;
    }

    public boolean isSupplier() {
        return role == Role.SUPPLIER;
    }

    public String getInitials() {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
