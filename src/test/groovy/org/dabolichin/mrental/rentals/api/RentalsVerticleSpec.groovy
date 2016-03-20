package org.dabolichin.mrental.rentals.api

import com.jayway.restassured.RestAssured
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import javaslang.Tuple2
import javaslang.collection.HashMap
import org.dabolichin.mrental.customers.repositories.CustomerInMemoryRepository
import org.dabolichin.mrental.movies.MovieType
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository
import org.dabolichin.mrental.rentals.commands.RentalCommandsHandler
import org.dabolichin.mrental.rentals.events.repositories.PointsEarnedEventRepository
import org.dabolichin.mrental.rentals.events.repositories.TransactionEventRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions

import java.time.LocalDate

import static com.jayway.restassured.RestAssured.config
import static com.jayway.restassured.RestAssured.given
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig
import static org.dabolichin.mrental.rentals.TestData.createCustomer
import static org.dabolichin.mrental.rentals.TestData.createMovie

@Stepwise
class RentalsVerticleSpec extends Specification {

    def @Shared Vertx vertx
    def @Shared Integer port
    def @Shared RentalsVerticle sut
    def @Shared movieInMemoryRepository = new MovieInMemoryRepository()
    def @Shared customerInMemoryRepository = new CustomerInMemoryRepository()
    def @Shared transactionEventRepository = new TransactionEventRepository()
    def @Shared pointsEarnedEventRepository = new PointsEarnedEventRepository()
    AsyncConditions conditions

    def setupSpec() {
        vertx = Vertx.vertx()
        ServerSocket socket = new ServerSocket(0)
        port = socket.getLocalPort()
        socket.close()
        DeploymentOptions options = new DeploymentOptions().setConfig(
                new JsonObject().put("http.port", port))
        sut = initSut(Router.router(vertx))
        vertx.deployVerticle(sut, options)

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = Integer.getInteger("http.port", port)
        RestAssured.config = config()
                .objectMapperConfig(
                objectMapperConfig().jackson2ObjectMapperFactory{cls, charset ->
                    Json.mapper
                })
    }

    def cleanupSpec() {
        RestAssured.reset()
        vertx.close()
    }


    def 'should rent movies and earn points'() {
        given:
            def customer = createCustomer()
            customerInMemoryRepository.save(customer)
            def movie1 = createMovie(UUID.randomUUID().toString(), MovieType.REGULAR)
            def movie2 = createMovie(UUID.randomUUID().toString(), MovieType.NEW_RELEASE)
            movieInMemoryRepository.save(movie1)
            movieInMemoryRepository.save(movie2)
            def rentMoviesCommandResource = new RentMovieCommandResource(
                    HashMap.of(
                            movie1.id(), new Tuple2(LocalDate.now(), LocalDate.now().plusDays(4)),
                            movie2.id(), new Tuple2(LocalDate.now(), LocalDate.now().plusDays(3))
                    ),
                    customer.id()
            )
        when:
            def rentalTransactionResource = given()
                    .body(Json.encodePrettily(rentMoviesCommandResource))
                    .request()
                    .post(sut.apiRoot + '/rentals')
                    .thenReturn()
                    .as(RentalTransactionResource.class)


        then:
            rentalTransactionResource.totalPrice == BigDecimal.valueOf(180)
            rentalTransactionResource.surcharge == BigDecimal.ZERO
            rentalTransactionResource.moviesRented.size() == 2
    }

    def 'should return movies'() {
        given:
            def transactionId = transactionEventRepository.all().first().transactionId()
            def movies = movieInMemoryRepository.all()
            def returnMoviesCommandResource = new ReturnMovieCommandResource(
                    movies.toMap { m -> new Tuple2(m.id(), LocalDate.now().plusDays(5)) }
            )
        when:
            def rentalTransactionResource = given()
                    .body(Json.encodePrettily(returnMoviesCommandResource))
                    .request()
                    .post(sut.apiRoot + '/rentals/' + transactionId + '/returns')
                    .thenReturn()
                    .as(RentalTransactionResource.class)
        then:
            rentalTransactionResource.id == transactionId
            rentalTransactionResource.moviesRented.size() == 2
            rentalTransactionResource.totalPrice == BigDecimal.valueOf(180)
            rentalTransactionResource.surcharge == BigDecimal.valueOf(110)
    }


    protected RentalsVerticle initSut(Router router) {
        return new RentalsVerticle(
                router,
                new RentalCommandsHandler(
                        movieInMemoryRepository,
                        customerInMemoryRepository,
                        pointsEarnedEventRepository,
                        transactionEventRepository
                )
        )
    }
}
