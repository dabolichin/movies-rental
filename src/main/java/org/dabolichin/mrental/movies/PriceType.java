package org.dabolichin.mrental.movies;

import java.math.BigDecimal;

public enum PriceType {
    BASIC(new BigDecimal(30)),
    PREMIUM(new BigDecimal(40));

    public final BigDecimal amount;

    PriceType(BigDecimal amount) {
        this.amount = amount;
    }

}
