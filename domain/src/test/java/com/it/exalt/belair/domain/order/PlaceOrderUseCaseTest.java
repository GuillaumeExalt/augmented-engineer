package com.it.exalt.belair.domain.order;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

class PlaceOrderUseCaseTest {

    @Test
    void shouldCreateOrderWhenItemAvailable() {
        // Given: a festival goer and an available item
        String festivalGoerId = "fg-1";
        // Item representation for the scenario
        String itemId = "item-1";

        // When: placing an order for the item
        // NOTE: production implementation is not provided yet.

        // Then: expect an order to be created. Red step: fail until implementation exists.
        fail("RED: Test not implemented against production code yet. Implement PlaceOrder use case.");
    }
}
