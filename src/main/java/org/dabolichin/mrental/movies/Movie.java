package org.dabolichin.mrental.movies;

import org.dabolichin.mrental.repositories.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.Period.between;

public class Movie implements Entity<String> {

    public final String id;

    public final String title;

    public final MovieType type;

    public Movie(String id, String title, MovieType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public BigDecimal calculatePrice(Integer daysRented) {
        return type.calculatePrice(daysRented);
    }

    public Integer calculateBonusPoints(Integer daysRented) {
        return type.calculatePoints(daysRented);
    }

    public Integer calculateBonusPoints(LocalDate from, LocalDate to){
        return calculateBonusPoints(between(from, to).getDays());
    }

    public BigDecimal calculatePrice(LocalDate from, LocalDate to) {
        return calculatePrice(between(from, to).getDays());
    }

    public BigDecimal calculateSurcharge(LocalDate from, LocalDate to, LocalDate plannedTo){
        return calculatePrice(from, to).subtract(calculatePrice(from, plannedTo)).max(BigDecimal.ZERO);
    }

    @Override
    public String id() {
        return id;
    }
}
