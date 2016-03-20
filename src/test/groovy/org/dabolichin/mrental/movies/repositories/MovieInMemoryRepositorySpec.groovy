package org.dabolichin.mrental.movies.repositories

import org.dabolichin.mrental.movies.Movie
import org.dabolichin.mrental.movies.MovieType
import org.dabolichin.mrental.repositories.CrudInMemoryRepositorySpec
import spock.genesis.generators.composites.PojoGenerator

import static spock.genesis.Gen.*

class MovieInMemoryRepositorySpec extends CrudInMemoryRepositorySpec<MovieInMemoryRepository> {
    @Override
    protected PojoGenerator generateEntity() {
        type(Movie,
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                any(MovieType.NEW_RELEASE, MovieType.OLD, MovieType.REGULAR)
        )
    }

    @Override
    protected MovieInMemoryRepository initSut() {
        return new MovieInMemoryRepository()
    }
}
