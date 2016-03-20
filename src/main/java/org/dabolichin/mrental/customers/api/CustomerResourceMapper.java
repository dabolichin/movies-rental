package org.dabolichin.mrental.customers.api;

import javaslang.control.Option;
import org.dabolichin.mrental.api.ResourceMapper;
import org.dabolichin.mrental.customers.Customer;

import java.util.UUID;

public class CustomerResourceMapper implements ResourceMapper<Customer, CustomerResource, String> {

    @Override
    public CustomerResource from(Customer customer) {
        return new CustomerResource(
                Option.of(customer.id),
                customer.firstName,
                customer.lastName,
                customer.email,
                customer.phone
        );
    }

    @Override
    public Customer to(CustomerResource customerResource) {
        return new Customer(
                customerResource.id.getOrElse(UUID.randomUUID().toString()),
                customerResource.firstName,
                customerResource.lastName,
                customerResource.email,
                customerResource.phone
        );
    }

    @Override
    public Customer to(String id, CustomerResource customerResource) {
        return new Customer (
                id,
                customerResource.firstName,
                customerResource.lastName,
                customerResource.email,
                customerResource.phone
        );
    }
}
