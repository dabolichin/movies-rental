package org.dabolichin.mrental.rentals.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javaslang.control.Option;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,
        getterVisibility= JsonAutoDetect.Visibility.NONE,
        isGetterVisibility= JsonAutoDetect.Visibility.NONE,
        setterVisibility= JsonAutoDetect.Visibility.NONE)
public class MovieRentalResource {
    public String movieId;
    public BigDecimal price;
    public LocalDate rentedOn;
    public LocalDate dueBy;
    public Option<LocalDate> returnedOn = Option.none();
    public BigDecimal surcharge = BigDecimal.ZERO;

    public MovieRentalResource() {
    }

    public MovieRentalResource(
            String movieId,
            BigDecimal price,
            LocalDate rentedOn,
            LocalDate dueBy,
            Option<LocalDate> returnedOn,
            BigDecimal surcharge) {
        this.movieId = movieId;
        this.price = price;
        this.rentedOn = rentedOn;
        this.dueBy = dueBy;
        this.returnedOn = returnedOn;
        this.surcharge = surcharge;
    }
}
