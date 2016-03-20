package org.dabolichin.mrental.rentals.api;

import org.dabolichin.mrental.rentals.RentalTransaction;
import org.dabolichin.mrental.rentals.commands.RentMoviesCommand;
import org.dabolichin.mrental.rentals.commands.ReturnMoviesCommand;

public class RentMovieCommandResourceMapper {

    public static RentalTransactionResource from(RentalTransaction rentalTransaction) {
        return new RentalTransactionResource(
                rentalTransaction.id(),
                rentalTransaction.totalPrice,
                rentalTransaction.moviesRented.map(m -> new MovieRentalResource(
                        m.movie.id(),
                        m.price,
                        m.rentedOn,
                        m.dueBy,
                        m.returnedOn,
                        m.surcharge
                )),
                rentalTransaction.customer.id,
                rentalTransaction.surcharge()
        );
    }

    public static RentMoviesCommand to(RentMovieCommandResource resource) {
        return new RentMoviesCommand(
                resource.moviesToRent,
                resource.customerId
        );
    }

    public static ReturnMoviesCommand to(String rentalTransationId, ReturnMovieCommandResource resource) {
        return new ReturnMoviesCommand(
                rentalTransationId,
                resource.moviesToReturn
        );
    }

}
