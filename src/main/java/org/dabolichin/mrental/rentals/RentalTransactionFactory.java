package org.dabolichin.mrental.rentals;

import javaslang.collection.List;
import javaslang.control.Match;
import javaslang.control.Option;
import org.dabolichin.mrental.customers.Customer;
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository;
import org.dabolichin.mrental.movies.Movie;
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository;
import org.dabolichin.mrental.rentals.events.MovieRented;
import org.dabolichin.mrental.rentals.events.MovieReturned;
import org.dabolichin.mrental.rentals.events.TransactionAwareEvent;
import org.dabolichin.mrental.rentals.events.repositories.TransactionEventRepository;

import static javaslang.control.Option.some;

public class RentalTransactionFactory {

    private final MovieInMemoryRepository moviesRepo;

    private final CustomerInMemoryRepository customerRepo;

    private final TransactionEventRepository transactionEventRepository;

    public RentalTransactionFactory(
            MovieInMemoryRepository moviesRepo,
            CustomerInMemoryRepository customerRepo,
            TransactionEventRepository transactionEventRepository) {
        this.moviesRepo = moviesRepo;
        this.customerRepo = customerRepo;
        this.transactionEventRepository = transactionEventRepository;
    }

    public Option<RentalTransaction> from(List<TransactionAwareEvent> events) {
        return events.foldLeft(
                events.headOption().map(e -> new RentalTransaction("")),
                this::applyEvent
        );
    }

    public Option<RentalTransaction> init(String transactionId) {
        return from(transactionEventRepository.allForTransaction(transactionId));
    }

    private Option<RentalTransaction> applyEvent(Option<RentalTransaction> rentalTransaction, TransactionAwareEvent event){
        return rentalTransaction.flatMap(rt -> applyEvent(rt, event));
    }

    public Option<RentalTransaction> applyEvent(RentalTransaction rentalTransaction, TransactionAwareEvent event){
        return Match.of(event)
                .whenType(MovieRented.class).then(e -> apply(rentalTransaction, e))
                .whenType(MovieReturned.class).then(e -> apply(rentalTransaction, e))
                .get();
    }

    public Option<RentalTransaction> applyEvents(RentalTransaction rentalTransaction, List<TransactionAwareEvent> events) {
        return events.foldLeft(
                Option.of(rentalTransaction),
                this::applyEvent
        );
    }

    private Option<RentalTransaction> apply(RentalTransaction rentalTransaction, MovieRented e) {

        Option<Customer> customer = customerRepo.one(e.customerId);
        Option<Movie> movie = moviesRepo.one(e.movieId);
        return customer
                .flatMap(c -> movie)
                .map(m -> new RentalTransaction(
                        e.rentalTransactionId,
                        rentalTransaction.totalPrice.add(e.price),
                        rentalTransaction.moviesRented.append(
                                new MovieRental(
                                        m,
                                        e.price,
                                        e.rentedOn,
                                        e.dueBy)
                        ),
                        customer.get())
                );
    }

    private Option<RentalTransaction> apply(RentalTransaction rentalTransaction, MovieReturned e) {
        rentalTransaction.moviesRented
                .filter(m -> m.movie.id().equals(e.movieId))
                .peek(m -> {
                            m.returnedOn = some(e.returnedOn);
                            m.surcharge = e.surcharge;
                        }
                );
        return Option.of(new RentalTransaction(
                rentalTransaction.id(),
                rentalTransaction.totalPrice,
                rentalTransaction.moviesRented,
                rentalTransaction.customer
        ));
    }
}
