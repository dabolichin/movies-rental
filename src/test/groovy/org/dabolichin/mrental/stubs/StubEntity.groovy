package org.dabolichin.mrental.stubs

import org.dabolichin.mrental.repositories.Entity

class StubEntity implements Entity<String> {

    public String id

    public String fakeAttribute1

    public Integer fakeAttribute2

    @Override
    String id() {
        return id;
    }
}
