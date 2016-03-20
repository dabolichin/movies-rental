package org.dabolichin.mrental.api;

public interface ResourceMapper<T, R, ID> {
    R from(T entity);
    T to(R resource);
    T to(ID id, R resource);
}
