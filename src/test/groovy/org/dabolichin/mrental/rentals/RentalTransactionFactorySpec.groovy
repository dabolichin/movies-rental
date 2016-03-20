package org.dabolichin.mrental.rentals

import javaslang.collection.List
import org.dabolichin.mrental.customers.Customer
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository
import org.dabolichin.mrental.movies.Movie
import org.dabolichin.mrental.movies.MovieType
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository
import org.dabolichin.mrental.rentals.events.MovieRented
import org.dabolichin.mrental.rentals.events.MovieReturned
import org.dabolichin.mrental.rentals.events.repositories.TransactionEventRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static org.dabolichin.mrental.rentals.TestData.createCustomer
import static org.dabolichin.mrental.rentals.TestData.createMovie

class RentalTransactionFactorySpec extends Specification {

    public static final String TRANSACTION_ID = '123'
    def @Shared customerInMemoryRepository = new CustomerInMemoryRepository()
    def @Shared movieInMemoryRepository = new MovieInMemoryRepository()
    def @Shared transactionEventRepository = new TransactionEventRepository()
    def @Shared RentalTransactionFactory sut = new RentalTransactionFactory(
            movieInMemoryRepository,
            customerInMemoryRepository,
            transactionEventRepository
    )

    def 'should create rental transaction from movie rented events'() {
        given:
            def customer = createCustomer()
            customerInMemoryRepository.save(customer)
            def movie1 = createMovie(UUID.randomUUID().toString(), MovieType.REGULAR)
            def movie2 = createMovie(UUID.randomUUID().toString(), MovieType.NEW_RELEASE)
            movieInMemoryRepository.save(movie1)
            movieInMemoryRepository.save(movie2)
            def movieRentedEvent1 = createMovieRentedEvent(customer, movie1, LocalDate.now().plusDays(1))
            def movieRentedEvent2 = createMovieRentedEvent(customer, movie2, LocalDate.now().plusDays(1))
        when:
            def rentalTransaction = sut.from(List.of(movieRentedEvent1, movieRentedEvent2))
        then:
            rentalTransaction.defined
            rentalTransaction.get().id() == TRANSACTION_ID
            rentalTransaction.get().customer.id() == customer.id()
            rentalTransaction.get().moviesRented.size() == 2
            rentalTransaction.get().moviesRented.first().movie.id == movie1.id()
            rentalTransaction.get().totalPrice == BigDecimal.valueOf(70)
            rentalTransaction.get().surcharge() == BigDecimal.ZERO
            rentalTransaction.get().isReturned() == false

    }

    def 'should finish rental transaction with movie returned events'() {
        given:
            def customer = createCustomer()
            customerInMemoryRepository.save(customer)
            def movie1 = createMovie(UUID.randomUUID().toString(), MovieType.REGULAR)
            def movie2 = createMovie(UUID.randomUUID().toString(), MovieType.NEW_RELEASE)
            movieInMemoryRepository.save(movie1)
            movieInMemoryRepository.save(movie2)
            def movieRentedEvent1 = createMovieRentedEvent(customer, movie1, LocalDate.now().plusDays(1))
            def movieRentedEvent2 = createMovieRentedEvent(customer, movie2, LocalDate.now().plusDays(1))
            transactionEventRepository.save(movieRentedEvent1)
            transactionEventRepository.save(movieRentedEvent2)
            def rentalTransaction = sut.from(List.of(movieRentedEvent1, movieRentedEvent2))
            def returnDate = LocalDate.now().plusDays(4);
            def movieReturnedEvent1 = createMovieReturnedEvent(
                    customer,
                    movie1,
                    returnDate,
                    movie1.calculateSurcharge(
                            LocalDate.now(),
                            returnDate,
                            LocalDate.now().plusDays(1)))
            def movieReturnedEvent2 = createMovieReturnedEvent(
                    customer,
                    movie2,
                    returnDate,
                    movie2.calculateSurcharge(
                            LocalDate.now(),
                            returnDate,
                            LocalDate.now().plusDays(1)))

        when:
            def loadedTransaction = sut.init(TRANSACTION_ID)
            def returnedTransaction = sut.applyEvents(loadedTransaction.get(), List.of(movieReturnedEvent1, movieReturnedEvent2))
        then:
            returnedTransaction.defined
            rentalTransaction.get().moviesRented.size() == 2
            rentalTransaction.get().totalPrice == BigDecimal.valueOf(70)
            returnedTransaction.get().surcharge() == BigDecimal.valueOf(150)
            returnedTransaction.get().isReturned()
    }

    private MovieRented createMovieRentedEvent(Customer customer, Movie movie, LocalDate rentDate) {
        new MovieRented(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                TRANSACTION_ID,
                customer.id(),
                movie.id,
                LocalDate.now(),
                rentDate,
                movie.calculatePrice(LocalDate.now(), rentDate)
        )
    }

    private MovieReturned createMovieReturnedEvent(Customer customer, Movie movie, LocalDate returnDate, BigDecimal surcharge) {
        new MovieReturned(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                TRANSACTION_ID,
                customer.id(),
                movie.id(),
                returnDate,
                surcharge
        )
    }

}
