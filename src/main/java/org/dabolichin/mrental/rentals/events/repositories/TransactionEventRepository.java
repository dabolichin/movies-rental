package org.dabolichin.mrental.rentals.events.repositories;

import javaslang.collection.List;
import org.dabolichin.mrental.rentals.events.Event;
import org.dabolichin.mrental.rentals.events.TransactionAwareEvent;
import org.dabolichin.mrental.repositories.CrudInMemoryRepository;

import java.util.Comparator;

public class TransactionEventRepository extends CrudInMemoryRepository<TransactionAwareEvent, String> {

    public List<TransactionAwareEvent> allForTransaction(String transactionId) {
        return entities
                .filter(t -> t._2.transactionId().equals(transactionId))
                .map(e -> e._2)
                .sorted(Comparator.comparing(Event::publishedOn))
                .toList();
    }
}
