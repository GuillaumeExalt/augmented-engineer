package com.it.exalt.belair.application.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceDrinkOrderControllerTest {

    @Test
    void shouldReturnCreatedOrderWhenSubmittingNonAlcoholicDrinkOrderScenario1() {
        // Given
        CreateDrinkOrderRequest request = new CreateDrinkOrderRequest("42", DrinkCategory.NON_ALCOHOLIC, 1, 0);
        PlaceDrinkOrderController controller = new PlaceDrinkOrderController();

        // When
        CreateDrinkOrderResponse response = controller.createOrder(request);

        // Then
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals("CREATED", response.result()),
                () -> assertEquals(0, response.drinkTokenCost())
        );
    }

    @Test
    void shouldReturnCreatedOrderWithSingleTokenDebitWhenSubmittingStandardAlcoholDrinkOrderScenario2() {
        // Given
        CreateDrinkOrderRequest request = new CreateDrinkOrderRequest("42", DrinkCategory.ALCOHOL_STANDARD, 1, 3);
        PlaceDrinkOrderController controller = new PlaceDrinkOrderController();

        // When
        CreateDrinkOrderResponse response = controller.createOrder(request);

        // Then
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals("CREATED", response.result()),
                () -> assertEquals(1, response.drinkTokenCost())
        );
    }

    @Test
    void shouldReturnValidationErrorWhenSubmittingPremiumAlcoholDrinkOrderWithInsufficientTokensScenario3() {
        // Given
        CreateDrinkOrderRequest request = new CreateDrinkOrderRequest("42", DrinkCategory.ALCOHOL_PREMIUM, 1, 1);
        PlaceDrinkOrderController controller = new PlaceDrinkOrderController();

        // When
        CreateDrinkOrderResponse response = controller.createOrder(request);

        // Then
        assertAll(
                () -> assertEquals(400, response.statusCode()),
                () -> assertEquals("VALIDATION_ERROR", response.result()),
                () -> assertTrue(response.errorMessage().contains("solde de jetons boisson insuffisant"))
        );
    }

    @Test
    void shouldReturnPendingOrderIdentifierWhenSubmittingAvailableDrinkOrderScenario4() {
        // Given
        CreateDrinkOrderRequest request = new CreateDrinkOrderRequest("festivalier-42", "article-1", "Mojito", 1, true);
        PlaceDrinkOrderController controller = new PlaceDrinkOrderController();

        // When
        CreateDrinkOrderResponse response = controller.createOrder(request);

        // Then
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals("EN_ATTENTE", response.orderStatus()),
                () -> assertNotNull(response.orderId()),
                () -> assertFalse(response.orderId().isBlank())
        );
    }
}