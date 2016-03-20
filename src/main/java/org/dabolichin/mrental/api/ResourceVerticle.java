package org.dabolichin.mrental.api;

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
import org.dabolichin.mrental.repositories.CrudRepository;

import java.util.function.Consumer;

public abstract class ResourceVerticle<T, R, ID> extends AbstractVerticle {

    public static final Logger log = LoggerFactory.getLogger(ResourceVerticle.class);

    private static final String API = "api";

    private static final String VERSION = "v1";

    public static final String apiRoot = "/" + API + "/" + VERSION;

    protected final CrudRepository<T, ID> crudRepository;

    protected final ResourceMapper<T, R, ID> resourceMapper;

    protected final Class<R> resourceClass;

    protected final Router router;

    public ResourceVerticle(CrudRepository crudRepository, ResourceMapper resourceMapper, Class<R> resourceClass, Router router) {
        this.crudRepository = crudRepository;
        this.resourceMapper = resourceMapper;
        this.resourceClass = resourceClass;
        this.router = router;
    }

    protected abstract String resourceUrlPart();

    protected abstract String resourceName();

    protected ID idFromString(String id){
        return (ID) id;
    }

    @Override
    public void start() throws Exception {

        setupRoutes(router);

        Json.mapper.registerModule(new JavaslangModule());
        Json.prettyMapper.registerModule(new JavaslangModule());

        vertx
            .createHttpServer()
            .requestHandler(router::accept)
            .listen(
                    config().getInteger("http.port", 8080)
            );

        log.info(resourceName() + " verticle has started");
    }

    protected void setupRoutes(Router router) {
        router.get(apiRoot + "/" + resourceUrlPart()).handler(this::all);
        router.get(apiRoot +  "/" + resourceUrlPart() + "/:id").handler(this::getOne);
        router.route(apiRoot +  "/" + resourceUrlPart() + "*").handler(BodyHandler.create());
        router.post(apiRoot +  "/" + resourceUrlPart()).handler(this::addOne);
        router.delete(apiRoot +  "/" + resourceUrlPart() + "/:id").handler(this::deleteOne);
    }

    protected void getOne(RoutingContext routingContext) {
        Option.of(routingContext.request().getParam("id"))
                .map(this::idFromString)
                .flatMap(crudRepository::one)
                .toTry()
                .map(resourceMapper::from)
                .andThen(c -> routingContext.response()
                        .putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(c)))
                .onFailure(o -> routingContext.response().setStatusCode(404).end());
    }

    protected void addOne(RoutingContext routingContext) {
        Try.of(() -> Json.decodeValue(routingContext.getBodyAsString(), resourceClass))
                .map(resourceMapper::to)
                .flatMap(c -> saveEntity(routingContext, c, 201, "Failed to add " + resourceName()));
    }

    protected void all(RoutingContext routingContext) {
        Try.of(crudRepository::all)
                .map(l -> l.map(resourceMapper::from))
                .andThen(l -> routingContext.response()
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(l)))
                .onFailure(onError(routingContext, "Failed to retrieve " + resourceName() + "s", 500));
    }

    protected void deleteOne(RoutingContext routingContext) {
        Option.of(routingContext.request().getParam("id"))
                .map(this::idFromString)
                .toTry()
                .andThen(id -> {
                    crudRepository.remove(id);
                    routingContext.response().setStatusCode(204).end();
                })
                .onFailure(o -> routingContext.response().setStatusCode(400).end());

    }

    protected Try<R> saveEntity(
            RoutingContext routingContext,
            T entity,
            int statusCode,
            String errorMessage){
        return crudRepository.save(entity)
                .map(resourceMapper::from)
                .andThen(c -> routingContext.response()
                        .setStatusCode(statusCode)
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(c)))
                .onFailure(onError(routingContext, errorMessage, 500));
    }

    protected static Consumer<Throwable> onError(RoutingContext routingContext, String message, int statusCode) {
        return f -> {
            log.error(message, f);
            routingContext.response().setStatusCode(statusCode).end();
        };
    }
}
