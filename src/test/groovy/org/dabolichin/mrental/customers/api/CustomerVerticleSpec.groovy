package org.dabolichin.mrental.customers.api

import io.vertx.ext.web.Router
import javaslang.control.Option
import org.dabolichin.mrental.api.ResourceVerticleSpec
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository
import spock.genesis.generators.composites.PojoGenerator

import static spock.genesis.Gen.*

class CustomerVerticleSpec extends ResourceVerticleSpec<CustomerVerticle> {
    @Override
    protected CustomerVerticle initSut(Router router) {
        return new CustomerVerticle(
                new CustomerInMemoryRepository(),
                new CustomerResourceMapper(),
                CustomerResource,
                router
        )
    }

    @Override
    protected List<String> assertAttributes() {
        return ['firstName', 'lastName', 'email', 'phone']
    }

    @Override
    protected PojoGenerator generateResource() {
        type(CustomerResource,
                value(Option.none()),
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                string(~/[A-Z]+([@][a-z]+)[.][a-z][0-9]+([A-Z][a-z][0-9]+)?/),
                string(~/[+][0-9]+?/)
        )
    }
}
