package org.dabolichin.mrental.movies.api

import io.vertx.ext.web.Router
import javaslang.control.Option
import org.dabolichin.mrental.api.ResourceVerticleSpec
import org.dabolichin.mrental.movies.MovieType
import org.dabolichin.mrental.movies.repositories.MovieInMemoryRepository
import spock.genesis.generators.composites.PojoGenerator

import static spock.genesis.Gen.*

class MoviesVerticleSpec extends ResourceVerticleSpec<MoviesVerticle> {
    @Override
    protected MoviesVerticle initSut(Router router) {
        return new MoviesVerticle(
                new MovieInMemoryRepository(),
                new MovieResourceMapper(),
                MovieResource,
                router
        )
    }

    @Override
    protected List<String> assertAttributes() {
        return ['title']
    }

    @Override
    protected PojoGenerator generateResource() {
        type(MovieResource,
                value(Option.none()),
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                any(MovieType.NEW_RELEASE, MovieType.REGULAR, MovieType.OLD)
        )
    }
}
