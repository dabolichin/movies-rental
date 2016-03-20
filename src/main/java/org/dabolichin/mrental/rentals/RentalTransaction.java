package org.dabolichin.mrental.rentals;

import javaslang.collection.List;
import org.dabolichin.mrental.customers.Customer;
import org.dabolichin.mrental.repositories.Entity;

import java.math.BigDecimal;

public class RentalTransaction implements Entity<String> {

    private final String id;

    public BigDecimal totalPrice = BigDecimal.ZERO;

    public List<MovieRental> moviesRented = List.empty();

    public Customer customer;

    public RentalTransaction(String id) {
        this.id = id;
    }

    public RentalTransaction(
            String id,
            BigDecimal totalPrice,
            List<MovieRental> moviesRented,
            Customer customer) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.moviesRented = moviesRented;
        this.customer = customer;
    }

    @Override
    public String id() {
        return id;
    }

    public boolean isReturned() {
        return moviesRented.forAll(m -> m.returnedOn.isDefined());
    }

    public BigDecimal surcharge() {
        return moviesRented.foldLeft(BigDecimal.ZERO, (sum, movie) -> sum.add(movie.surcharge));
    }
}
