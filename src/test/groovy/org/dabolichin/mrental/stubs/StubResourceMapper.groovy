package org.dabolichin.mrental.stubs

import javaslang.control.Option
import org.dabolichin.mrental.api.ResourceMapper

class StubResourceMapper implements ResourceMapper<StubEntity, StubResource, String>{
    @Override
    StubResource from(StubEntity entity) {
        return new StubResource(
                id: Option.of(entity.id),
                fakeAttribute1: entity.fakeAttribute1,
                fakeAttribute2: entity.fakeAttribute2
        )
    }

    @Override
    StubEntity to(StubResource resource) {
        return new StubEntity(
                id: resource.id.getOrElse(UUID.randomUUID().toString()),
                fakeAttribute1: resource.fakeAttribute1,
                fakeAttribute2: resource.fakeAttribute2
        )
    }

    @Override
    StubEntity to(String id, StubResource resource) {
        return new StubEntity(
                id,
                resource.fakeAttribute1,
                resource.fakeAttribute2
        )
    }
}
