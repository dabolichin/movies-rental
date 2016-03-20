package org.dabolichin.mrental.repositories

import spock.genesis.generators.composites.PojoGenerator
import spock.lang.Specification

abstract class CrudInMemoryRepositorySpec<R extends CrudInMemoryRepository> extends Specification {


    def "should save and retrieve entity"() {
        setup:
            R sut = initSut()
        when:
            sut.save(stubEntity)
            def retrievedEntity = sut.one(stubEntity.id)
        then:
            retrievedEntity.isDefined()
            retrievedEntity.get().id == stubEntity.id
        where:
            stubEntity << generateEntity().take(10)
    }

    def "should save and remove entity"() {
        setup:
            R sut = initSut()
        when:
            sut.save(stubEntity)
            sut.remove(stubEntity.id)
        then:
            sut.one(stubEntity.id)
                    .isEmpty()
        where:
            stubEntity << generateEntity().take(10)
    }

    protected abstract PojoGenerator generateEntity()

    protected abstract R initSut()

}
