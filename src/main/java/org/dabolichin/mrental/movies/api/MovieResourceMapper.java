package org.dabolichin.mrental.movies.api;

import javaslang.control.Option;
import org.dabolichin.mrental.api.ResourceMapper;
import org.dabolichin.mrental.movies.Movie;

import java.util.UUID;

public class MovieResourceMapper implements ResourceMapper<Movie, MovieResource, String> {
    @Override
    public MovieResource from(Movie movie) {
        return new MovieResource(
                Option.of(movie.id),
                movie.title,
                movie.type
        );
    }

    @Override
    public Movie to(MovieResource resource) {
        return new Movie(
                resource.id.getOrElse(UUID.randomUUID().toString()),
                resource.title,
                resource.type
        );
    }

    @Override
    public Movie to(String id, MovieResource resource) {
        return new Movie(
                id,
                resource.title,
                resource.type
        );
    }
}
