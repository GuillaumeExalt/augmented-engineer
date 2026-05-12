package com.it.exalt.belair.domain.order;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.it.exalt.belair.domain.order.model.CreatedOrder;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.model.PlaceDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.RequestedItem;
import com.it.exalt.belair.domain.order.port.out.AvailableDrinkStockPort;
import com.it.exalt.belair.domain.order.port.out.OrderIdPort;
import com.it.exalt.belair.domain.order.usecase.PlaceDrinkOrderUseCase;

class PlaceDrinkOrderUseCaseTest {

    private final PlaceDrinkOrderUseCase useCase = new PlaceDrinkOrderUseCase(
            new InMemoryAvailableDrinkStockPort(),
            new FixedOrderIdPort()
    );

    @Test
    void shouldCreatePendingOrderWithIdentifierWhenOrderingOneAvailableDrinkScenario4() {
        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(
                "festivalier-42",
                Map.of("Mojito", 10),
                new RequestedItem("Mojito", 1)
        );

        CreatedOrder createdOrder = useCase.handle(command);

        assertAll(
                () -> assertEquals(OrderStatus.EN_ATTENTE, createdOrder.status()),
                () -> assertFalse(createdOrder.orderId().isBlank())
        );
    }

    private static final class InMemoryAvailableDrinkStockPort implements AvailableDrinkStockPort {
        @Override
        public boolean isAvailable(Map<String, Integer> availableStock, String articleName, int requestedQuantity) {
            Integer availableQuantity = availableStock.get(articleName);
            return availableQuantity != null && requestedQuantity > 0 && availableQuantity >= requestedQuantity;
        }
    }

    private static final class FixedOrderIdPort implements OrderIdPort {
        @Override
        public String nextOrderId() {
            return "commande-42";
        }
    }

}
