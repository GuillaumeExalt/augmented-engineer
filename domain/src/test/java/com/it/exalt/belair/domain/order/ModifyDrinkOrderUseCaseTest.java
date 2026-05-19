package com.it.exalt.belair.domain.order;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

class ModifyDrinkOrderUseCaseTest {

    private final ModifyDrinkOrderUseCase useCase = new ModifyDrinkOrderUseCase();

    @Test
    void shouldUpdateNonAcquittedOrderWhenModifiedCostRespectsFestivalierTokenBalancesScenario1() {
        DrinkOrder initialOrder = new DrinkOrder(
                "commande-42",
                "festivalier-42",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1))
        );
        List<DrinkOrderLine> updatedLines = List.of(
                new DrinkOrderLine("Mojito", 2),
                new DrinkOrderLine("Sandwich", 1)
        );

        ModifyDrinkOrderResult result = useCase.handle(new ModifyDrinkOrderCommand(
                initialOrder,
                updatedLines,
                2,
                1,
                3,
                2
        ));

        assertAll(
                () -> assertFalse(result.hasChangeRequest()),
                () -> assertNull(result.changeRequest()),
                () -> assertEquals(updatedLines, result.order().lines()),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1)), initialOrder.lines())
        );
    }

    @Test
    void shouldRejectModificationWhenModifiedCostExceedsFestivalierTokenBalancesScenario2() {
        DrinkOrder initialOrder = new DrinkOrder(
                "commande-43",
                "festivalier-42",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1))
        );
        List<DrinkOrderLine> updatedLines = List.of(
                new DrinkOrderLine("Mojito", 3),
                new DrinkOrderLine("Sandwich", 2)
        );

        Throwable thrown = captureThrowable(() -> useCase.handle(new ModifyDrinkOrderCommand(
                initialOrder,
                updatedLines,
                3,
                2,
                2,
                1
        )));

        assertAll(
                () -> assertInstanceOf(IllegalStateException.class, thrown),
                () -> assertEquals("SOLDE_JETONS_INSUFFISANT", thrown == null ? null : thrown.getMessage()),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1)), initialOrder.lines())
        );
    }

    @Test
    void shouldCreateChangeRequestWhenOrderIsAlreadyAcquittedScenario3() {
        DrinkOrder acquittedOrder = new DrinkOrder(
                "commande-44",
                "festivalier-42",
                OrderStatus.ACQUITTEE,
                List.of(new DrinkOrderLine("Mojito", 1))
        );
        List<DrinkOrderLine> updatedLines = List.of(
                new DrinkOrderLine("Mojito", 2),
                new DrinkOrderLine("Sandwich", 1)
        );

        ModifyDrinkOrderResult result = useCase.handle(new ModifyDrinkOrderCommand(
                acquittedOrder,
                updatedLines,
                2,
                1,
                0,
                0
        ));

        assertAll(
                () -> assertTrue(result.hasChangeRequest()),
                () -> assertEquals(acquittedOrder, result.order()),
                () -> assertEquals("commande-44", result.changeRequest().orderId()),
                () -> assertEquals(updatedLines, result.changeRequest().requestedLines())
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
}
