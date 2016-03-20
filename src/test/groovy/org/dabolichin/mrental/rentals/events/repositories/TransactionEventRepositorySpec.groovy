package org.dabolichin.mrental.rentals.events.repositories

import org.dabolichin.mrental.rentals.events.MovieRented
import org.dabolichin.mrental.repositories.CrudInMemoryRepositorySpec
import spock.genesis.generators.composites.PojoGenerator

import java.time.LocalDate
import java.time.LocalDateTime

import static spock.genesis.Gen.*

class TransactionEventRepositorySpec extends CrudInMemoryRepositorySpec<TransactionEventRepository> {

    public static final String TRANSACTION_ID = '123'

    @Override
    protected PojoGenerator generateEntity() {
        type(MovieRented,
                string(~/[A-Z][a-z]+( [A-Z][a-z]+)?/),
                value(LocalDateTime.now()),
                value(TRANSACTION_ID),
                string(~/[A-Z][a-z]+?/),
                string(~/[A-Z][a-z]+?/),
                value(LocalDate.now()),
                value(LocalDate.now()),
                value(BigDecimal.ZERO)
        )
    }

    @Override
    protected TransactionEventRepository initSut() {
        return new TransactionEventRepository()
    }

    def 'should list all events for same transaction and order by date'() {
        setup:
            TransactionEventRepository sut = initSut()
            def eventsCounter = 0;
        when:
            sut.save(event)
            def events = sut.allForTransaction(TRANSACTION_ID)
            eventsCounter++
        then:
            events.size() == eventsCounter
            events.forEach{
                it.transactionId() == TRANSACTION_ID
            }
            events.map { it.publishedOn() }.max().get() == events.last().publishedOn()
            events.map { it.publishedOn() }.min().get() == events.head().publishedOn()

        where:
            event << generateEntity().take(10)
    }
}
