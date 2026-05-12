package com.it.exalt.belair.domain.order;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
    void shouldCreatePendingOrderAndDecrementStockWhenAvailableQuantityIsSufficientScenario4() {
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("Mojito", 10);

        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(
                "festivalier-42",
                availableStock,
                new RequestedItem("Mojito", 2)
        );

        CreatedOrder createdOrder = useCase.handle(command);

        assertAll(
                () -> assertEquals(OrderStatus.EN_ATTENTE, createdOrder.status()),
                () -> assertFalse(createdOrder.orderId().isBlank()),
                () -> assertEquals(8, availableStock.get("Mojito"))
        );
    }

    @Test
    void shouldRaiseStockInsuffisantErrorWhenRequestedQuantityExceedsAvailableStockScenario7() {
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("Mojito", 1);

        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(
                "festivalier-42",
                availableStock,
                new RequestedItem("Mojito", 2)
        );

        Throwable thrown = captureThrowable(() -> useCase.handle(command));

        assertAll(
                () -> assertInstanceOf(IllegalStateException.class, thrown),
                () -> assertEquals("STOCK_INSUFFISANT", thrown == null ? null : thrown.getMessage()),
                () -> assertEquals(1, availableStock.get("Mojito"))
        );
    }

    @Test
    void shouldRaiseArticleInconnuErrorWhenRequestedDrinkDoesNotExistInCatalogScenario8() {
        PlaceDrinkOrderCommand command = new PlaceDrinkOrderCommand(
                "festivalier-42",
                Map.of(),
                new RequestedItem("Champagne", 1)
        );

        Throwable thrown = captureThrowable(() -> useCase.handle(command));

        assertAll(
                () -> assertInstanceOf(IllegalArgumentException.class, thrown),
                () -> assertEquals("ARTICLE_INCONNU", thrown == null ? null : thrown.getMessage())
        );
    }

    private static Throwable captureThrowable(Executable executable) {
        try {
            executable.execute();
            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }

    private static final class InMemoryAvailableDrinkStockPort implements AvailableDrinkStockPort {
        @Override
        public boolean isAvailable(Map<String, Integer> availableStock, String articleName, int requestedQuantity) {
            return requestedQuantity > 0;
        }
    }

    private static final class FixedOrderIdPort implements OrderIdPort {
        @Override
        public String nextOrderId() {
            return "commande-42";
        }
    }

}
