package org.dabolichin.mrental.rentals.events;

public interface TransactionAwareEvent extends Event {
    String transactionId();
}
