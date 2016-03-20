package org.dabolichin.mrental.api

import com.jayway.restassured.RestAssured
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import spock.genesis.generators.composites.PojoGenerator
import spock.lang.Shared
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.*
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig
import static org.hamcrest.Matchers.equalTo

abstract class ResourceVerticleSpec<V extends ResourceVerticle> extends Specification {

    def @Shared Vertx vertx
    def @Shared Integer port
    def @Shared V sut

    protected abstract V initSut(Router router)

    protected abstract List<String> assertAttributes()

    protected abstract PojoGenerator generateResource()

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

    def 'should add new entity and then delete'() {
        when:
            def savedEntity = given()
                            .body(Json.encodePrettily(stubEntity))
                            .request()
                            .post(sut.apiRoot + '/' + sut.resourceUrlPart())
                            .thenReturn()
                            .as(sut.resourceClass)

            def getResponse = get(sut.apiRoot + '/' + sut.resourceUrlPart() + '/' + savedEntity.id.get())

            def delResponse = delete(sut.apiRoot + '/' + sut.resourceUrlPart() + '/' + savedEntity.id.get())

            def getAfterDelResponse = get(sut.apiRoot + '/' + sut.resourceUrlPart() + '/' + savedEntity.id.get())

        then:
            assertAttributes().forEach {
                savedEntity[it] == stubEntity[it]
            }
            savedEntity.id.isDefined()

            def valResponse = getResponse
                                .then()
                                .assertThat()
                                .statusCode(200)
                                .body('id', equalTo(savedEntity.id.get()))

            assertAttributes().forEach{
                valResponse.body(it, equalTo(stubEntity[it]))
            }

            delResponse
                    .then()
                    .assertThat().statusCode(204)

            getAfterDelResponse
                    .then()
                    .assertThat()
                    .statusCode(404)
        where:
            stubEntity << generateResource().take(10)
    }

}
