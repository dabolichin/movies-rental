package org.dabolichin.mrental.rentals.commands;

import javaslang.Tuple2;
import javaslang.collection.Seq;
import javaslang.control.Option;
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository;
import org.dabolichin.mrental.movies.Movie;
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository;
import org.dabolichin.mrental.rentals.MovieRental;
import org.dabolichin.mrental.rentals.RentalTransaction;
import org.dabolichin.mrental.rentals.RentalTransactionFactory;
import org.dabolichin.mrental.rentals.events.MovieRented;
import org.dabolichin.mrental.rentals.events.MovieReturned;
import org.dabolichin.mrental.rentals.events.PointsEarned;
import org.dabolichin.mrental.rentals.events.TransactionAwareEvent;
import org.dabolichin.mrental.rentals.events.repositories.PointsEarnedEventRepository;
import org.dabolichin.mrental.rentals.events.repositories.TransactionEventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RentalCommandsHandler {

    private final MovieInMemoryRepository movieInMemoryRepository;

    private final CustomerInMemoryRepository customerInMemoryRepository;

    private final TransactionEventRepository transactionEventRepository;

    private final PointsEarnedEventRepository pointsEarnedEventRepository;

    private final RentalTransactionFactory rentalTransactionFactory;

    public RentalCommandsHandler(
            MovieInMemoryRepository movieInMemoryRepository,
            CustomerInMemoryRepository customerInMemoryRepository,
            PointsEarnedEventRepository pointsEarnedEventRepository, TransactionEventRepository transactionEventRepository) {
        this.movieInMemoryRepository = movieInMemoryRepository;
        this.customerInMemoryRepository = customerInMemoryRepository;
        this.pointsEarnedEventRepository = pointsEarnedEventRepository;
        this.transactionEventRepository = transactionEventRepository;
        this.rentalTransactionFactory = new RentalTransactionFactory(
                movieInMemoryRepository,
                customerInMemoryRepository,
                transactionEventRepository
        );
    }

    public Option<RentalTransaction> handleRentMoviesCommand(RentMoviesCommand rentMoviesCommand){
        String transactionId = UUID.randomUUID().toString();
        Seq<TransactionAwareEvent> movieRentedEvents = rentMoviesCommand.moviesToRent
                                                .map(m -> new Tuple2<>(movieInMemoryRepository.one(m._1), m._2))
                                                .map(m -> createMovieRentedEvent(
                                                        transactionId,
                                                        rentMoviesCommand.customerId,
                                                        m._1.get(),
                                                        m._2._1,
                                                        m._2._2));
        Seq<PointsEarned> pointsEarnedEvents = rentMoviesCommand.moviesToRent
                                                .map(e -> createPointsEarnedEvent(
                                                        rentMoviesCommand.customerId,
                                                        movieInMemoryRepository.one(e._1).get(),
                                                        e._2._1,
                                                        e._2._2
                                                ));

        movieRentedEvents.forEach(e -> transactionEventRepository.save(e));
        pointsEarnedEvents.forEach(e -> pointsEarnedEventRepository.save(e));

        return rentalTransactionFactory.from(movieRentedEvents.toList());
    }

    public Option<RentalTransaction> handleReturnMoviesCommand(ReturnMoviesCommand returnMoviesCommand){
        Option<RentalTransaction> rentalTransaction = rentalTransactionFactory.init(returnMoviesCommand.rentalTransationId);
        return rentalTransaction
                .map(rt -> returnMoviesCommand.moviesToReturn
                        .map(m -> new Tuple2<>(
                            rentalTransaction.get().moviesRented
                                    .find(r -> r.movie.id().equals(m._1)),
                            m._2))
                        .<TransactionAwareEvent>map(m -> createMovieReturnedEvent(
                                rt.id(),
                                rt.customer.id,
                                m._1.get(),
                                m._2
                        )))
                .peek(es -> es.forEach(e ->
                        transactionEventRepository.save(e)))
                .flatMap(es -> rentalTransactionFactory.applyEvents(
                        rentalTransaction.get(),
                        es.toList()));
    }

    private MovieRented createMovieRentedEvent(
            String transactionId,
            String customerId,
            Movie movie,
            LocalDate from,
            LocalDate to) {
        return new MovieRented(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                transactionId,
                customerId,
                movie.id,
                from,
                to,
                movie.calculatePrice(from, to)
        );
    }

    private MovieReturned createMovieReturnedEvent(
            String transactionId,
            String customerId,
            MovieRental movieRental,
            LocalDate returnDate) {
        return new MovieReturned(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                transactionId,
                customerId,
                movieRental.movie.id(),
                returnDate,
                movieRental.movie.calculateSurcharge(movieRental.rentedOn, returnDate, movieRental.dueBy)
        );
    }

    private PointsEarned createPointsEarnedEvent(
            String customerId,
            Movie movie,
            LocalDate from,
            LocalDate to){
        return new PointsEarned(
                UUID.randomUUID().toString(),
                movie.calculateBonusPoints(from, to),
                customerId,
                LocalDateTime.now(),
                movie.id()
        );
    }
}
