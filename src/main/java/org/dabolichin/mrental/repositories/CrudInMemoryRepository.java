package org.dabolichin.mrental.repositories;

import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;

public abstract class CrudInMemoryRepository<T extends Entity<ID>, ID> implements CrudRepository<T, ID> {

    protected Map<ID, T> entities = HashMap.empty();

    @Override
    public Seq<T> all() {
        return entities.values();
    }

    @Override
    public Try<T> save(T entity) {
        return Option.of(entity)
                .toTry()
                .andThen(e -> this.entities = entities.put(e.id(), e));
    }

    @Override
    public Option<T> one(ID id) {
        return entities.get(id);
    }

    @Override
    public void remove(ID id) {
        this.entities = entities.remove(id);
    }
}
