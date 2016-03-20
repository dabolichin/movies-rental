package org.dabolichin.mrental.rentals.commands;

import javaslang.Tuple2;
import javaslang.collection.Map;

import java.time.LocalDate;

public class RentMoviesCommand {
    public final Map<String, Tuple2<LocalDate, LocalDate>> moviesToRent;
    public final String customerId;

    public RentMoviesCommand(Map<String, Tuple2<LocalDate, LocalDate>> moviesToRent, String customerId) {
        this.moviesToRent = moviesToRent;
        this.customerId = customerId;
    }
}
