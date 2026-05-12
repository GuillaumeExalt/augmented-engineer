package com.it.exalt.belair.domain.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlaceDrinkOrderUseCaseTest {

    @Test
    void shouldAcceptOrderWithoutChangingDrinkTokensWhenOrderingNonAlcoholicDrinkScenario1() {
        // Given
        FestivalGoer festivalGoer = new FestivalGoer("festivalier-42", 0);
        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(festivalGoer.id(), DrinkCategory.NON_ALCOHOLIC, 1);
        PlaceDrinkOrderUseCase useCase = new PlaceDrinkOrderUseCase();

        // When
        DrinkOrderResult result = useCase.handle(festivalGoer, command);

        // Then
        assertAll(
                () -> assertEquals(OrderDecision.ACCEPTED, result.decision()),
                () -> assertEquals(0, festivalGoer.drinkTokenBalance())
        );
    }

    @Test
    void shouldAcceptOrderAndDecreaseDrinkTokensByOneWhenOrderingStandardAlcoholDrinkScenario2() {
        // Given
        FestivalGoer festivalGoer = new FestivalGoer("festivalier-42", 2);
        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(festivalGoer.id(), DrinkCategory.ALCOHOL_STANDARD, 1);
        PlaceDrinkOrderUseCase useCase = new PlaceDrinkOrderUseCase();

        // When
        DrinkOrderResult result = useCase.handle(festivalGoer, command);

        // Then
        assertAll(
                () -> assertEquals(OrderDecision.ACCEPTED, result.decision()),
                () -> assertEquals(1, festivalGoer.drinkTokenBalance())
        );
    }

    @Test
    void shouldRejectOrderWithoutChangingDrinkTokensWhenOrderingPremiumAlcoholDrinkWithInsufficientBalanceScenario3() {
        // Given
        FestivalGoer festivalGoer = new FestivalGoer("festivalier-42", 1);
        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(festivalGoer.id(), DrinkCategory.ALCOHOL_PREMIUM, 1);
        PlaceDrinkOrderUseCase useCase = new PlaceDrinkOrderUseCase();

        // When
        DrinkOrderResult result = useCase.handle(festivalGoer, command);

        // Then
        assertAll(
                () -> assertEquals(OrderDecision.REJECTED, result.decision()),
                () -> assertEquals(1, festivalGoer.drinkTokenBalance())
        );
    }

    @Test
    void shouldCreatePendingOrderWithIdentifierWhenOrderingAvailableArticleScenario4() {
        // Given
        FestivalGoer festivalGoer = new FestivalGoer("festivalier-42", 0);
        AvailableDrinkArticle mojito = new AvailableDrinkArticle("article-1", "Mojito", true);
        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(festivalGoer.id(), mojito.id(), 1);
        PlaceDrinkOrderUseCase useCase = new PlaceDrinkOrderUseCase();

        // When
        DrinkOrderResult result = useCase.handle(festivalGoer, mojito, command);

        // Then
        assertAll(
                () -> assertEquals(OrderStatus.EN_ATTENTE, result.order().status()),
                () -> assertNotNull(result.order().id()),
                () -> assertFalse(result.order().id().isBlank())
        );
    }
}
