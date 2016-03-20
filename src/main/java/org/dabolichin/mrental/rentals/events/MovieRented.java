package org.dabolichin.mrental.rentals.events;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovieRented implements TransactionAwareEvent {

    private final String id;

    private final LocalDateTime publishedOn;

    public final String rentalTransactionId;

    public final String customerId;

    public final String movieId;

    public final LocalDate rentedOn;

    public final LocalDate dueBy;

    public final BigDecimal price;

    public MovieRented(
            String id,
            LocalDateTime publishedOn,
            String rentalTransactionId,
            String customerId,
            String movieId,
            LocalDate rentedOn,
            LocalDate dueBy,
            BigDecimal price) {
        this.id = id;
        this.publishedOn = publishedOn;
        this.rentalTransactionId = rentalTransactionId;
        this.customerId = customerId;
        this.movieId = movieId;
        this.rentedOn = rentedOn;
        this.dueBy = dueBy;
        this.price = price;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String transactionId() {
        return rentalTransactionId;
    }

    @Override
    public LocalDateTime publishedOn() {
        return publishedOn;
    }
}
