package org.dabolichin.mrental.rentals;

import javaslang.control.Option;
import org.dabolichin.mrental.movies.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class MovieRental {
    public final Movie movie;
    public final BigDecimal price;
    public final LocalDate rentedOn;
    public final LocalDate dueBy;
    public Option<LocalDate> returnedOn = Option.none();
    public BigDecimal surcharge = BigDecimal.ZERO;

    public MovieRental(Movie movie, BigDecimal price, LocalDate rentedOn, LocalDate dueBye) {
        this.movie = movie;
        this.price = price;
        this.rentedOn = rentedOn;
        this.dueBy = dueBye;
    }

    public Integer extraDays() {
        return returnedOn
                .map(d -> Math.max(Period.between(this.dueBy, d).getDays(), 0))
                .getOrElse(0);
    }
}
