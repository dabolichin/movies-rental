package org.dabolichin.mrental.rentals.commands

import javaslang.Tuple2
import javaslang.collection.HashMap
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository
import org.dabolichin.mrental.movies.MovieType
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository
import org.dabolichin.mrental.rentals.events.repositories.PointsEarnedEventRepository
import org.dabolichin.mrental.rentals.events.repositories.TransactionEventRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate

import static org.dabolichin.mrental.rentals.TestData.createCustomer
import static org.dabolichin.mrental.rentals.TestData.createMovie

@Stepwise
class RentalCommandsHandlerSpec extends Specification {

    def @Shared customerInMemoryRepository = new CustomerInMemoryRepository()
    def @Shared movieInMemoryRepository = new MovieInMemoryRepository()
    def @Shared transactionEventRepository = new TransactionEventRepository()
    def @Shared pointsEarnedEventRepository = new PointsEarnedEventRepository()
    def @Shared RentalCommandsHandler sut = new RentalCommandsHandler(
            movieInMemoryRepository,
            customerInMemoryRepository,
            pointsEarnedEventRepository,
            transactionEventRepository
    )

    def 'should handle movies rented command and create rental transaction'() {
        given:
            def customer = createCustomer()
            customerInMemoryRepository.save(customer)
            def movie1 = createMovie(UUID.randomUUID().toString(), MovieType.REGULAR)
            def movie2 = createMovie(UUID.randomUUID().toString(), MovieType.NEW_RELEASE)
            movieInMemoryRepository.save(movie1)
            movieInMemoryRepository.save(movie2)
            def rentMoviesCommand = new RentMoviesCommand(
                    HashMap.of(
                            movie1.id(), new Tuple2(LocalDate.now(), LocalDate.now().plusDays(4)),
                            movie2.id(), new Tuple2(LocalDate.now(), LocalDate.now().plusDays(3))
                    ),
                    customer.id()
            )
        when:
            def rentalTransaction = sut.handleRentMoviesCommand(rentMoviesCommand)
        then:
            rentalTransaction.defined
            rentalTransaction.get().totalPrice == BigDecimal.valueOf(180)
            rentalTransaction.get().surcharge() == BigDecimal.ZERO
            rentalTransaction.get().moviesRented.size() == 2
            transactionEventRepository.all().size() == 2
            pointsEarnedEventRepository.all().size() == 2
    }

    def 'should handle movies returned command and finish rental transaction'() {
        given:
            def transactionId = transactionEventRepository.all().first().transactionId()
            def movies = movieInMemoryRepository.all()
            def returnMoviesCommand = new ReturnMoviesCommand(
                    transactionId,
                    movies.toMap { m -> new Tuple2(m.id(), LocalDate.now().plusDays(5)) }
            )

        when:
            def rentalTransaction = sut.handleReturnMoviesCommand(returnMoviesCommand)

        then:
            rentalTransaction.defined
            rentalTransaction.get().id() == transactionId
            rentalTransaction.get().moviesRented.size() == 2
            rentalTransaction.get().totalPrice == BigDecimal.valueOf(180)
            rentalTransaction.get().surcharge() == BigDecimal.valueOf(110)
            rentalTransaction.get().returned
    }


}
