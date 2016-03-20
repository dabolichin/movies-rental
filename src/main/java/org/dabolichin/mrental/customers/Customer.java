package org.dabolichin.mrental.customers;

import org.dabolichin.mrental.repositories.Entity;

public class Customer implements Entity<String> {

    public final String id;

    public final String firstName;

    public final String lastName;

    public final String email;

    public final String phone;

    public Customer(String id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String id() {
        return id;
    }

}
