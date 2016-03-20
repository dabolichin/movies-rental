package org.dabolichin.mrental.rentals.commands;

import javaslang.collection.Map;

import java.time.LocalDate;

public class ReturnMoviesCommand {
    public final String rentalTransationId;
    public final Map<String, LocalDate> moviesToReturn;

    public ReturnMoviesCommand(String rentalTransationId, Map<String, LocalDate> moviesToReturn) {
        this.rentalTransationId = rentalTransationId;
        this.moviesToReturn = moviesToReturn;
    }
}
