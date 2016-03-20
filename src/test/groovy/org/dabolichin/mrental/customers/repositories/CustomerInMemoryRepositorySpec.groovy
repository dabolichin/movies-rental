package org.dabolichin.mrental.customers.repositories

import org.dabolichin.mrental.customers.Customer
import org.dabolichin.mrental.repositories.CrudInMemoryRepository
import org.dabolichin.mrental.repositories.CrudInMemoryRepositorySpec
import spock.genesis.generators.composites.PojoGenerator

import static spock.genesis.Gen.string
import static spock.genesis.Gen.type

class CustomerInMemoryRepositorySpec extends CrudInMemoryRepositorySpec {
    @Override
    protected PojoGenerator generateEntity() {
        type(Customer,
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                string(~/[A-Z]+([@][a-z]+)[.][a-z][0-9]+([A-Z][a-z][0-9]+)?/),
                string(~/[+][0-9]+?/)
        )
    }

    @Override
    protected CrudInMemoryRepository initSut() {
        return new CustomerInMemoryRepository()
    }
}
