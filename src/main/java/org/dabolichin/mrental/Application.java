package org.dabolichin.mrental;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import org.dabolichin.mrental.customers.api.CustomerResource;
import org.dabolichin.mrental.customers.api.CustomerResourceMapper;
import org.dabolichin.mrental.customers.api.CustomerVerticle;
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository;
import org.dabolichin.mrental.movies.api.MovieResource;
import org.dabolichin.mrental.movies.api.MovieResourceMapper;
import org.dabolichin.mrental.movies.api.MoviesVerticle;
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository;
import org.dabolichin.mrental.rentals.api.RentalsVerticle;
import org.dabolichin.mrental.rentals.commands.RentalCommandsHandler;
import org.dabolichin.mrental.rentals.events.repositories.PointsEarnedEventRepository;
import org.dabolichin.mrental.rentals.events.repositories.TransactionEventRepository;

public class Application {

    public static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        MovieInMemoryRepository movieRepo = new MovieInMemoryRepository();
        CustomerInMemoryRepository customerRepo = new CustomerInMemoryRepository();

        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        Verticle customerVerticle = new CustomerVerticle(
                customerRepo,
                new CustomerResourceMapper(),
                CustomerResource.class,
                router);

        Verticle moviesVerticle = new MoviesVerticle(
                movieRepo,
                new MovieResourceMapper(),
                MovieResource.class,
                router
        );

        Verticle rentalsVerticle = new RentalsVerticle(
                router,
                new RentalCommandsHandler(
                        movieRepo,
                        customerRepo,
                        new PointsEarnedEventRepository(),
                        new TransactionEventRepository()
                )
        );

        vertx.deployVerticle(customerVerticle);
        vertx.deployVerticle(moviesVerticle);
        vertx.deployVerticle(rentalsVerticle);

        log.info("Movies rental application has started");

    }
}
