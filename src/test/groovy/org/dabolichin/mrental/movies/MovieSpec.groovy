package org.dabolichin.mrental.movies

import spock.lang.Specification

import java.time.LocalDate

import static java.time.LocalDate.now
import static org.dabolichin.mrental.movies.MovieType.*
import static org.dabolichin.mrental.movies.MovieType.REGULAR

class MovieSpec extends Specification {

    def 'should calculate price for different movie types'() {
        given:
            def movie = new Movie(
                    UUID.randomUUID().toString(),
                    'some title',
                    movieType
            )
        expect:
            movie.calculatePrice(daysRented) == price

        where:
            movieType             | daysRented | price

            OLD         | 7          | BigDecimal.valueOf(90)
            REGULAR     | 5          | BigDecimal.valueOf(90)
            REGULAR     | 2          | BigDecimal.valueOf(30)
            NEW_RELEASE | 1          | BigDecimal.valueOf(40)
    }

    def 'should calculate surcharge'() {
        given:
            def movie = new Movie(
                    UUID.randomUUID().toString(),
                    'some title',
                    movieType
            )
        expect:
            movie.calculateSurcharge(rentedOn, returnedOn, dueBye)
        where:
            movieType   | rentedOn           | dueBye | returnedOn        | price

            REGULAR     | now().minusDays(3) | now()  | now().plusDays(1) | BigDecimal.valueOf(30)
            NEW_RELEASE | now().minusDays(1) | now()  | now().plusDays(2) | BigDecimal.valueOf(80)
    }

}
