package org.dabolichin.mrental.rentals.events;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovieReturned implements TransactionAwareEvent {

    private final String id;

    private final LocalDateTime publishedOn;

    public final String rentalTransactionId;

    public final String customerId;

    public final String movieId;

    public final LocalDate returnedOn;

    public final BigDecimal surcharge;

    public MovieReturned(
            String id,
            LocalDateTime publishedOn, String rentalTransactionId,
            String customerId,
            String movieId,
            LocalDate returnedOn,
            BigDecimal surcharge) {
        this.id = id;
        this.publishedOn = publishedOn;
        this.rentalTransactionId = rentalTransactionId;
        this.customerId = customerId;
        this.movieId = movieId;
        this.returnedOn = returnedOn;
        this.surcharge = surcharge;
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
