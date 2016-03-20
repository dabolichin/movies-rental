package org.dabolichin.mrental.rentals.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javaslang.collection.List;

import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,
        getterVisibility= JsonAutoDetect.Visibility.NONE,
        isGetterVisibility= JsonAutoDetect.Visibility.NONE,
        setterVisibility= JsonAutoDetect.Visibility.NONE)
public class RentalTransactionResource {

    public String id;

    public BigDecimal totalPrice = BigDecimal.ZERO;

    public List<MovieRentalResource> moviesRented = List.empty();

    public String customerId;

    public BigDecimal surcharge = BigDecimal.ZERO;

    public RentalTransactionResource() {
    }

    public RentalTransactionResource(
            String id,
            BigDecimal totalPrice,
            List<MovieRentalResource> moviesRented,
            String customerId,
            BigDecimal surcharge) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.moviesRented = moviesRented;
        this.customerId = customerId;
        this.surcharge = surcharge;
    }
}
