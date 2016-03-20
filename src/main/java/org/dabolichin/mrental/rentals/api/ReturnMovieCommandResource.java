package org.dabolichin.mrental.rentals.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javaslang.collection.Map;

import java.time.LocalDate;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,
        getterVisibility= JsonAutoDetect.Visibility.NONE,
        isGetterVisibility= JsonAutoDetect.Visibility.NONE,
        setterVisibility= JsonAutoDetect.Visibility.NONE)
public class ReturnMovieCommandResource {
    public Map<String, LocalDate> moviesToReturn;

    public ReturnMovieCommandResource() {}

    public ReturnMovieCommandResource(Map<String, LocalDate> moviesToReturn) {
        this.moviesToReturn = moviesToReturn;
    }
}
