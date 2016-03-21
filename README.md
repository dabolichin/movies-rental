# movies-rental
This task is actually coming originally from famous book by Martin Fowler -
http://martinfowler.com/books/refactoring.htm

Here I have decided to make things a bit more interesting and instead of going with traditional CRUD-style approach implemented
movie rentals and returns using [event-sourcing](http://martinfowler.com/eaaDev/EventSourcing.html).
Making it work and model properly took me most of the time (I have spent more than 4 hours on this, but it was fun).
Here we have 3 main events:
* movie rented
* movie returned
* points earned

Replaying first those events in chronological order builds current state for any
rental transaction. In CRUD-style approach Rental Transaction would have to be mutated when movies are returned.

In real life proper event-storing mechanism together with even-handlers should be used.
Here results of events processing are pretty straight-forward.

# Frameworks used
[Vert.x](http://vertx.io/) - was used as a toolkit for building RESTful API.
I prefer it over Spring-boot or Dropwizard as it comes without magic and without huge number of dependencies.
It is event-driven, non-blocking and using [Netty](http://netty.io/) under the hood.

[Javaslang](http://www.javaslang.io/) - was used in order to leverage functional programming patterns and immutable data structures
and make code more expressive.
Javaslang is currently one of the best functional libraries for Java 8.

[Spock](http://www.javaslang.io/) - used for testing in BDD style.

[REST Assured](https://github.com/jayway/rest-assured) - used for testing RESTful interfaces.

# Running application

For building and running the solution use:
```
./gradlew run
```
This will start web-application with following end-points:
* http://localhost:8080/api/v1/customers - for adding/retrieving customers
* http://localhost:8080/api/v1/movies - for adding/retrieving movies
* http://localhost:8080/api/v1/rentals - for renting/returning movies

For adding new customer use:
```
curl -X POST -d '{   "id" : null,   "firstName" : "Tom",   "lastName" : "Johnson",   "email" : "tom@email.com",   "phone" : "+781389899007" }' http://localhost:8080/api/v1/customers
```

For adding new movie use:
```
curl -X POST -d '{   "id" : null,   "title" : "Matrix",   "type" : "OLD" }' http://localhost:8080/api/v1/movies
```

For renting movies use (replace ids with proper):
```
curl -X POST -d '{   "moviesToRent" : {     "a37a7cd9-6ab4-4090-88db-b578da3805d5" : [ "2016-03-21", "2016-03-25" ],     "c095ca00-8075-4bc6-9a0f-57a0c364a792" : [ "2016-03-21", "2016-03-24" ]   },   "customerId" : "d34b00d8-5dfb-4352-8857-f59c5c14bfcd" }' http://localhost:8080/api/v1/rentals
```

For returning movies use (replace ids with proper):
```
curl -X POST -d '{   "moviesToReturn" : {     "a37a7cd9-6ab4-4090-88db-b578da3805d5" : "2016-03-26",     "c095ca00-8075-4bc6-9a0f-57a0c364a792" : "2016-03-26"   } }' http://localhost:8080/api/v1/rentals/fee3154d-a8f8-4b44-890e-c2b598be4238/returns
```