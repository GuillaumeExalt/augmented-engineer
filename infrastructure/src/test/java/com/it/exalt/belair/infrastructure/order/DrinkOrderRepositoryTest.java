package com.it.exalt.belair.infrastructure.order;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.infrastructure.order.repository.JpaDrinkOrderRepository;

class DrinkOrderRepositoryTest {

    @Test
    void shouldSaveNewOrderAndFindItByIdentifierWithPendingStatusAndLinesScenario9() {
        try (RepositoryTestContext context = repositoryUnderTest()) {
            DrinkOrderRepository repository = context.repository();
            DrinkOrder order = new DrinkOrder(
                    "commande-42",
                    "festivalier-42",
                    OrderStatus.EN_ATTENTE,
                    List.of(
                            new DrinkOrderLine("Mojito", 2),
                            new DrinkOrderLine("Eau plate", 1)
                    )
            );

            repository.save(order);
            DrinkOrder persistedOrder = repository.findById(order.orderId()).orElseThrow();

            assertAll(
                    () -> assertEquals(OrderStatus.EN_ATTENTE, persistedOrder.status()),
                    () -> assertEquals(2, persistedOrder.lines().size()),
                    () -> assertEquals("Mojito", persistedOrder.lines().getFirst().articleName()),
                    () -> assertEquals(2, persistedOrder.lines().getFirst().quantity()),
                    () -> assertEquals("Eau plate", persistedOrder.lines().get(1).articleName()),
                    () -> assertEquals(1, persistedOrder.lines().get(1).quantity())
            );
        }
    }

    @Test
    void shouldUpdatePersistedOrderStatusToReadyScenario10() {
        try (RepositoryTestContext context = repositoryUnderTest()) {
            DrinkOrderRepository repository = context.repository();
            DrinkOrder order = new DrinkOrder(
                    "commande-43",
                    "festivalier-42",
                    OrderStatus.EN_ATTENTE,
                    List.of(new DrinkOrderLine("Mojito", 2))
            );

            repository.save(order);
            repository.updateStatus(order.orderId(), OrderStatus.PRETE);

            DrinkOrder persistedOrder = repository.findById(order.orderId()).orElseThrow();

            assertEquals(OrderStatus.PRETE, persistedOrder.status());
        }
    }

    @Test
    void shouldFindOnlyPendingOrdersForFestivalierScenario11() {
        try (RepositoryTestContext context = repositoryUnderTest()) {
            DrinkOrderRepository repository = context.repository();
            repository.save(new DrinkOrder(
                    "commande-44",
                    "festivalier-42",
                    OrderStatus.EN_ATTENTE,
                    List.of(new DrinkOrderLine("Mojito", 1))
            ));
            repository.save(new DrinkOrder(
                    "commande-45",
                    "festivalier-42",
                    OrderStatus.EN_ATTENTE,
                    List.of(new DrinkOrderLine("Eau plate", 1))
            ));
            repository.save(new DrinkOrder(
                    "commande-46",
                    "festivalier-42",
                    OrderStatus.PRETE,
                    List.of(new DrinkOrderLine("Mojito", 1))
            ));

            List<DrinkOrder> pendingOrders = repository.findByFestivalierIdAndStatus("festivalier-42", OrderStatus.EN_ATTENTE);

            assertAll(
                    () -> assertEquals(2, pendingOrders.size()),
                    () -> assertTrue(pendingOrders.stream().allMatch(order -> order.status() == OrderStatus.EN_ATTENTE))
            );
        }
    }

    private static RepositoryTestContext repositoryUnderTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
                "belair-drink-order-persistence",
                Map.of("jakarta.persistence.jdbc.url", "jdbc:h2:mem:belair-drink-order-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1")
        );
        return new RepositoryTestContext(new JpaDrinkOrderRepository(entityManagerFactory), entityManagerFactory);
    }

    private record RepositoryTestContext(DrinkOrderRepository repository, EntityManagerFactory entityManagerFactory)
            implements AutoCloseable {
        @Override
        public void close() {
            entityManagerFactory.close();
        }
    }
}