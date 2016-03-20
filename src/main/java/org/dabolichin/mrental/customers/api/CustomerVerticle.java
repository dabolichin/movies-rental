package org.dabolichin.mrental.customers.api;

import io.vertx.ext.web.Router;
import org.dabolichin.mrental.api.ResourceMapper;
import org.dabolichin.mrental.api.ResourceVerticle;
import org.dabolichin.mrental.customers.Customer;
import org.dabolichin.mrental.repositories.CrudRepository;

public class CustomerVerticle extends ResourceVerticle<Customer, CustomerResource, String> {

    public CustomerVerticle(CrudRepository crudRepository,
                            ResourceMapper resourceMapper,
                            Class<CustomerResource> resourceClass,
                            Router router) {
        super(crudRepository, resourceMapper, resourceClass, router);
    }

    @Override
    protected String resourceUrlPart() {
        return "customers";
    }

    @Override
    protected String resourceName() {
        return "customer";
    }
}

