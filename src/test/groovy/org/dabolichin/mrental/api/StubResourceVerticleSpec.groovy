package org.dabolichin.mrental.api

import io.vertx.ext.web.Router
import javaslang.control.Option
import org.dabolichin.mrental.stubs.StubInMemoryRepository
import org.dabolichin.mrental.stubs.StubResource
import org.dabolichin.mrental.stubs.StubResourceMapper
import org.dabolichin.mrental.stubs.StubVerticle
import spock.genesis.generators.composites.PojoGenerator

import static spock.genesis.Gen.*

class StubResourceVerticleSpec extends ResourceVerticleSpec<StubVerticle>{
    @Override
    protected StubVerticle initSut(Router router) {
        return new StubVerticle(
                new StubInMemoryRepository(),
                new StubResourceMapper(),
                StubResource,
                router
        )
    }

    @Override
    protected List<String> assertAttributes() {
        return ['fakeAttribute1', 'fakeAttribute2']
    }

    @Override
    protected PojoGenerator generateResource() {
        type(StubResource,
                id: value(Option.none()),
                fakeAttribute1: string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                fakeAttribute2: integer(1, 100)
        )
    }
}
