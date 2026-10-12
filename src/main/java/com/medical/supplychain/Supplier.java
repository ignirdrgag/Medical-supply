package com.medical.supplychain;

import java.io.Serializable;
import java.util.UUID;

public final class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String contact;
    private final String email;

    public Supplier(String name, String contact, String email) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = name;
        this.contact = contact;
        this.email = email;
    }

    public String getId() {
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
