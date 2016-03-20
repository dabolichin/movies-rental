package org.dabolichin.mrental.movies;

import java.math.BigDecimal;

public enum MovieType {
    REGULAR(
            PriceType.BASIC,
            daysRented -> 1,
            (priceType, daysRented) -> firstDaysBasePrice(priceType.amount, 3, daysRented)
    ),
    OLD(
            PriceType.BASIC,
            daysRented -> 1,
            (priceType, daysRented) -> firstDaysBasePrice(priceType.amount, 5, daysRented)
    ),
    NEW_RELEASE(
            PriceType.PREMIUM,
            daysRented -> 2,
            (priceType, daysRented) -> priceType.amount.multiply(new BigDecimal(daysRented))
    );

    private final PriceType priceType;

    private final BonusProgram bonusProgram;

    private final PriceStrategy priceStrategy;

    @FunctionalInterface
    public interface BonusProgram {
        Integer calculatePoints(Integer daysRented);
    }

    @FunctionalInterface
    public interface PriceStrategy {
        BigDecimal calculatePrice(PriceType priceType, Integer daysRented);
    }

    MovieType(PriceType priceType, BonusProgram bonusProgram, PriceStrategy priceStrategy) {
        this.priceType = priceType;
        this.bonusProgram = bonusProgram;
        this.priceStrategy = priceStrategy;
    }

    public Integer calculatePoints(Integer daysRented) {
        return bonusProgram.calculatePoints(daysRented);
    }

    public BigDecimal calculatePrice(Integer daysRented) {
        return priceStrategy.calculatePrice(this.priceType, daysRented);
    }

    private static BigDecimal firstDaysBasePrice(
            BigDecimal baseCharge,
            Integer daysWithBaseCharge,
            Integer daysRented) {
        return baseCharge.multiply(
                new BigDecimal(Math.max(daysRented - daysWithBaseCharge + 1, 1)));
    }
}



