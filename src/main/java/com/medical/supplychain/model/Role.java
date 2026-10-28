package com.medical.supplychain.model;

public enum Role {
    ADMIN("Administrateur", "/admin/dashboard", "admin"),
    STAFF("Agent de stock", "/staff/dashboard", "staff"),
    MANAGER("Responsable", "/manager/dashboard", "manager"),
    DOCTOR("Medecin", "/doctor/dashboard", "doctor"),
    SUPPLIER("Fournisseur", "/supplier/dashboard", "supplier");

    private final String label;
    private final String homePath;
    private final String cssClass;

    Role(String label, String homePath, String cssClass) {
        this.label = label;
        this.homePath = homePath;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return name();
    }

    public String getHomePath() {
        return homePath;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static Role from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le role est obligatoire.");
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Role inconnu : " + value);
        }
    }
}
