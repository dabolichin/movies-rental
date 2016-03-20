package org.dabolichin.mrental.rentals

import org.dabolichin.mrental.customers.Customer
import org.dabolichin.mrental.movies.Movie
import org.dabolichin.mrental.movies.MovieType

class TestData {
    public static Movie createMovie(String id, MovieType type) {
        new Movie(
                id,
                'Title',
                type
        )
    }
    public static Customer createCustomer() {
        new Customer(
                UUID.randomUUID().toString(),
                'Name',
                'Surname',
                'email@email.com',
                '1234567'
        )
    }
}
