package org.dabolichin.mrental.repositories

import org.dabolichin.mrental.stubs.StubEntity
import org.dabolichin.mrental.stubs.StubInMemoryRepository
import spock.genesis.generators.composites.PojoGenerator

import static spock.genesis.Gen.*

class StubInMemoryRepositorySpec extends CrudInMemoryRepositorySpec<StubInMemoryRepository>{
    @Override
    protected PojoGenerator generateEntity() {
        type(StubEntity,
                id: string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                fakeAttribute1: string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                fakeAttribute2: integer(1, 100)
        )
    }

    @Override
    protected StubInMemoryRepository initSut() {
        return new StubInMemoryRepository()
    }
}
