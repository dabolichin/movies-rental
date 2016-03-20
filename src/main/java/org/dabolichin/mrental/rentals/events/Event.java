package org.dabolichin.mrental.rentals.events;

import org.dabolichin.mrental.repositories.Entity;

import java.time.LocalDateTime;

public interface Event extends Entity<String> {
    LocalDateTime publishedOn();
}
