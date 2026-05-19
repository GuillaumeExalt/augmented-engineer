package com.it.exalt.belair.domain.order;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.InsufficientFestivalierTokenBalanceException;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderChangeRequestRepository;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

class ModifyDrinkOrderUseCaseTest {

    private final InMemoryDrinkOrderRepository drinkOrderRepository = new InMemoryDrinkOrderRepository();
    private final InMemoryDrinkOrderChangeRequestRepository drinkOrderChangeRequestRepository =
            new InMemoryDrinkOrderChangeRequestRepository();
    private final ModifyDrinkOrderUseCase useCase = new ModifyDrinkOrderUseCase(
            drinkOrderRepository,
            drinkOrderChangeRequestRepository
    );

    @Test
    void shouldUpdateNonAcquittedOrderWhenModifiedCostRespectsFestivalierTokenBalancesScenario1() {
        DrinkOrder existingOrder = new DrinkOrder(
                "commande-42",
                "festivalier-42",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1))
        );
        List<DrinkOrderLine> updatedLines = List.of(
                new DrinkOrderLine("Mojito", 1),
                new DrinkOrderLine("Burger", 2)
        );
        ModifyDrinkOrderCommand command = new ModifyDrinkOrderCommand(
                existingOrder,
                updatedLines,
                3,
                2,
                4,
                3
        );

        ModifyDrinkOrderResult result = useCase.handle(command);

        assertAll(
                () -> assertEquals(updatedLines, result.order().lines()),
                () -> assertEquals(OrderStatus.EN_ATTENTE, result.order().status()),
                () -> assertFalse(result.hasChangeRequest()),
                () -> assertEquals(result.order(), drinkOrderRepository.savedOrder),
                () -> assertNull(drinkOrderChangeRequestRepository.savedChangeRequest)
        );
    }

    @Test
    void shouldRejectModificationWhenModifiedCostExceedsFestivalierTokenBalancesScenario2() {
        DrinkOrder existingOrder = new DrinkOrder(
                "commande-43",
                "festivalier-43",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1))
        );
        ModifyDrinkOrderCommand command = new ModifyDrinkOrderCommand(
                existingOrder,
                List.of(new DrinkOrderLine("Mojito", 3), new DrinkOrderLine("Burger", 2)),
                5,
                3,
                2,
                1
        );

        Throwable thrown = captureThrowable(() -> useCase.handle(command));

        assertAll(
                () -> assertInstanceOf(InsufficientFestivalierTokenBalanceException.class, thrown),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1)), existingOrder.lines()),
                () -> assertEquals(OrderStatus.EN_ATTENTE, existingOrder.status()),
                () -> assertNull(drinkOrderRepository.savedOrder),
                () -> assertNull(drinkOrderChangeRequestRepository.savedChangeRequest)
        );
    }

    @Test
    void shouldCreateChangeRequestWhenOrderIsAlreadyAcquittedScenario3() {
        DrinkOrder existingOrder = new DrinkOrder(
                "commande-44",
                "festivalier-44",
                OrderStatus.ACQUITTEE,
                List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1))
        );
        List<DrinkOrderLine> requestedLines = List.of(
                new DrinkOrderLine("Mojito", 1),
                new DrinkOrderLine("Burger", 2)
        );
        ModifyDrinkOrderCommand command = new ModifyDrinkOrderCommand(
                existingOrder,
                requestedLines,
                3,
                2,
                5,
                5
        );

        ModifyDrinkOrderResult result = useCase.handle(command);

        assertAll(
                () -> assertEquals(existingOrder.lines(), result.order().lines()),
                () -> assertTrue(result.hasChangeRequest()),
                () -> assertEquals("commande-44", result.changeRequest() == null ? null : result.changeRequest().orderId()),
                () -> assertEquals(requestedLines, result.changeRequest() == null ? List.of() : result.changeRequest().requestedLines()),
                () -> assertNull(drinkOrderRepository.savedOrder),
                () -> assertEquals(result.changeRequest(), drinkOrderChangeRequestRepository.savedChangeRequest)
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

    private static final class InMemoryDrinkOrderRepository implements DrinkOrderRepository {
        private DrinkOrder savedOrder;

        @Override
        public void save(DrinkOrder order) {
            savedOrder = order;
        }

        @Override
        public Optional<DrinkOrder> findById(String orderId) {
            return Optional.empty();
        }

        @Override
        public void updateStatus(String orderId, OrderStatus status) {
        }

        @Override
        public List<DrinkOrder> findByFestivalierIdAndStatus(String festivalierId, OrderStatus status) {
            return List.of();
        }
    }

    private static final class InMemoryDrinkOrderChangeRequestRepository implements DrinkOrderChangeRequestRepository {
        private DrinkOrderChangeRequest savedChangeRequest;

        @Override
        public void save(DrinkOrderChangeRequest changeRequest) {
            savedChangeRequest = changeRequest;
        }
    }
}
