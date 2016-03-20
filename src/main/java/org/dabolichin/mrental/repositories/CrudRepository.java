package org.dabolichin.mrental.repositories;

import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;

public interface CrudRepository<T, ID> {
    Seq<T> all();

    Try<T> save(T customer);

    Option<T> one(ID id);

    void remove(ID id);
}
