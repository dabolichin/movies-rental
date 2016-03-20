package org.dabolichin.mrental.stubs

import io.vertx.ext.web.Router
import org.dabolichin.mrental.api.ResourceMapper
import org.dabolichin.mrental.api.ResourceVerticle
import org.dabolichin.mrental.repositories.CrudRepository

class StubVerticle extends ResourceVerticle<StubEntity, StubResource, String> {

    StubVerticle(CrudRepository crudRepository,
                 ResourceMapper resourceMapper,
                 Class<StubResource> resourceClass,
                 Router router) {
        super(crudRepository, resourceMapper, resourceClass, router)
    }

    @Override
    protected String resourceUrlPart() {
        return 'stubs'
    }

    @Override
    protected String resourceName() {
        return 'stub'
    }
}
