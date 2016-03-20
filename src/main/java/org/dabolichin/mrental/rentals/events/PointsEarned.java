package org.dabolichin.mrental.rentals.events;

import java.time.LocalDateTime;

public class PointsEarned implements Event {

    private final String id;

    private final LocalDateTime publishedOn;

    private final String customerId;

    private final Integer points;

    private final String movieId;

    public PointsEarned(
            String id,
            Integer points,
            String customerId,
            LocalDateTime publishedOn,
            String movieId) {
        this.points = points;
        this.customerId = customerId;
        this.id = id;
        this.publishedOn = publishedOn;
        this.movieId = movieId;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public LocalDateTime publishedOn() {
        return publishedOn;
    }
}
