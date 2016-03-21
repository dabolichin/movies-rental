package org.dabolichin.mrental.movies.api;

import io.vertx.ext.web.Router;
import org.dabolichin.mrental.api.ResourceMapper;
import org.dabolichin.mrental.api.ResourceVerticle;
import org.dabolichin.mrental.movies.Movie;
import org.dabolichin.mrental.repositories.CrudRepository;

public class MoviesVerticle extends ResourceVerticle<Movie, MovieResource, String> {
    public MoviesVerticle(CrudRepository crudRepository,
                          ResourceMapper resourceMapper,
                          Class<MovieResource> resourceClass,
                          Router router) {
        super(crudRepository, resourceMapper, resourceClass, router);
    }

    @Override
    protected String resourceUrlPart() {
        return "movies";
    }

    @Override
    protected String resourceName() {
        return "movie";
    }
}
