package org.dabolichin.mrental.rentals.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javaslang.Tuple2;
import javaslang.collection.Map;

import java.time.LocalDate;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,
        getterVisibility= JsonAutoDetect.Visibility.NONE,
        isGetterVisibility= JsonAutoDetect.Visibility.NONE,
        setterVisibility= JsonAutoDetect.Visibility.NONE)
public class RentMovieCommandResource {
    public Map<String, Tuple2<LocalDate, LocalDate>> moviesToRent;
    public String customerId;

    public RentMovieCommandResource() {
    }

    public RentMovieCommandResource(Map<String, Tuple2<LocalDate, LocalDate>> moviesToRent, String customerId) {
        this.moviesToRent = moviesToRent;
        this.customerId = customerId;
    }
}
