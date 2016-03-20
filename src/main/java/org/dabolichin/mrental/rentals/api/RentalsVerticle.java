package org.dabolichin.mrental.rentals.api;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import javaslang.control.Option;
import javaslang.control.Try;
import javaslang.jackson.datatype.JavaslangModule;
import org.dabolichin.mrental.rentals.RentalTransaction;
import org.dabolichin.mrental.rentals.commands.RentMoviesCommand;
import org.dabolichin.mrental.rentals.commands.RentalCommandsHandler;
import org.dabolichin.mrental.rentals.commands.ReturnMoviesCommand;

import java.util.function.Consumer;

public class RentalsVerticle extends AbstractVerticle {

    public static final Logger log = LoggerFactory.getLogger(RentalsVerticle.class);

    private static final String API = "api";

    private static final String VERSION = "v1";

    public static final String apiRoot = "/" + API + "/" + VERSION;

    private final Router router;

    private final RentalCommandsHandler rentalCommandsHandler;

    public RentalsVerticle(Router router, RentalCommandsHandler rentalCommandsHandler) {
        this.router = router;
        this.rentalCommandsHandler = rentalCommandsHandler;
    }

    @Override
    public void start() throws Exception {

        setupRoutes(router);

        Json.mapper.registerModule(new JavaslangModule());
        Json.prettyMapper.registerModule(new JavaslangModule());
        Json.mapper.registerModule(new JavaTimeModule());
        Json.prettyMapper.registerModule(new JavaTimeModule());
        Json.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Json.prettyMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080)
                );

        log.info("Rentals verticle has started");
    }

    private void setupRoutes(Router router) {
        router.route(apiRoot +  "/rentals*").handler(BodyHandler.create());
        router.post(apiRoot +  "/rentals").handler(this::rentMovies);
        router.post(apiRoot +  "/rentals/:id/returns").handler(this::returnMovies);
    }

    private void rentMovies(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();
        log.info(body);
        Try.of(() -> Json.decodeValue(body, RentMovieCommandResource.class))
                .map(RentMovieCommandResourceMapper::to)
                .flatMap(c -> handleRentMoviesCommand(routingContext, c, 201, "Failed to rent movies"))
                .onFailure(onError(routingContext, "Failed to return movies", 500));
    }

    private void returnMovies(RoutingContext routingContext) {
        Option<String> transactionId = Option.of(routingContext.request().getParam("id"));
        String body = routingContext.getBodyAsString();
        log.info(body);
        transactionId
                .toTry()
                .map(id -> Json.decodeValue(body, ReturnMovieCommandResource.class))
                .map(r -> RentMovieCommandResourceMapper.to(transactionId.get(), r))
                .flatMap(c -> handleReturnMoviesCommand(routingContext, c, 201, "Failed to return movies"))
                .onFailure(onError(routingContext, "Failed to return movies", 500));
    }

    private Try<RentalTransactionResource> handleReturnMoviesCommand(
            RoutingContext routingContext,
            ReturnMoviesCommand command,
            int statusCode,
            String errorMessage) {
        return respondWith(
                rentalCommandsHandler.handleReturnMoviesCommand(command),
                routingContext,
                statusCode,
                errorMessage);
    }

    private Try<RentalTransactionResource> handleRentMoviesCommand(
            RoutingContext routingContext,
            RentMoviesCommand command,
            int statusCode,
            String errorMessage){
        return respondWith(
                rentalCommandsHandler.handleRentMoviesCommand(command),
                routingContext,
                statusCode,
                errorMessage);
    }

    private static Consumer<Throwable> onError(RoutingContext routingContext, String message, int statusCode) {
        return f -> {
            log.error(message, f);
            routingContext.response().setStatusCode(statusCode).end();
        };
    }

    private Try<RentalTransactionResource> respondWith(
            Option<RentalTransaction> rentalTransaction,
            RoutingContext routingContext,
            int statusCode,
            String errorMessage) {
        return rentalTransaction
                .map(RentMovieCommandResourceMapper::from)
                .toTry()
                .andThen(c -> routingContext.response()
                        .setStatusCode(statusCode)
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(c)))
                .andThen(c -> log.info(Json.encodePrettily(c)))
                .onFailure(onError(routingContext, errorMessage, 500));
    }
}
