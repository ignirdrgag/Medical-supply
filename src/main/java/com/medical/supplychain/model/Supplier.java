package com.medical.supplychain.model;

public final class Supplier {
    private final int id;
    private final String name;
    private final String contact;
    private final String email;

    public Supplier(int id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }
}
