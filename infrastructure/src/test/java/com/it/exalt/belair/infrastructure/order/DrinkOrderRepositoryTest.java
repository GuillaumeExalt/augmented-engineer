package com.it.exalt.belair.infrastructure.order;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderChangeRequestRepository;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.infrastructure.order.repository.JpaDrinkOrderChangeRequestRepository;
import com.it.exalt.belair.infrastructure.order.repository.JpaDrinkOrderRepository;

class DrinkOrderRepositoryTest {

    @Test
    void shouldReplacePersistedLinesWhenSavingValidatedNonAcquittedOrderModificationScenario1() {
        try (RepositoryTestContext context = modificationRepositoryUnderTest()) {
            DrinkOrderRepository repository = context.repository();
            DrinkOrder existingOrder = new DrinkOrder(
                    "commande-50",
                    "festivalier-50",
                    OrderStatus.EN_ATTENTE,
                    List.of(
                            new DrinkOrderLine("Mojito", 2),
                            new DrinkOrderLine("Eau plate", 1)
                    )
            );
            repository.save(existingOrder);

            DrinkOrder updatedOrder = new DrinkOrder(
                    "commande-50",
                    "festivalier-50",
                    OrderStatus.EN_ATTENTE,
                    List.of(
                            new DrinkOrderLine("Mojito", 1),
                            new DrinkOrderLine("Eau plate", 3)
                    )
            );

            Throwable thrown = captureThrowable(() -> repository.save(updatedOrder));
            DrinkOrder persistedOrder = repository.findById(existingOrder.orderId()).orElseThrow();

            assertAll(
                    () -> assertNull(thrown),
                    () -> assertEquals(OrderStatus.EN_ATTENTE, persistedOrder.status()),
                    () -> assertEquals(updatedOrder.lines(), persistedOrder.lines())
            );
        }
    }

    @Test
    void shouldPersistPendingChangeRequestAndPublishBartenderNotificationForAcquittedOrderScenario2() {
        try (RepositoryTestContext context = modificationRepositoryUnderTest()) {
            DrinkOrderRepository repository = context.repository();
            repository.save(new DrinkOrder(
                    "commande-51",
                    "festivalier-51",
                    OrderStatus.ACQUITTEE,
                    List.of(new DrinkOrderLine("Mojito", 2))
            ));
            DrinkOrderChangeRequest changeRequest = new DrinkOrderChangeRequest(
                    "commande-51",
                    List.of(
                            new DrinkOrderLine("Mojito", 1),
                            new DrinkOrderLine("Eau plate", 1)
                    )
            );
            ChangeRequestNotificationSpy notificationSpy = new ChangeRequestNotificationSpy();

            Throwable thrown = captureThrowable(() -> changeRequestRepositoryUnderTest(
                    context.entityManagerFactory(),
                    notificationSpy
            ).save(changeRequest));

            assertAll(
                    () -> assertNull(thrown),
                    () -> assertEquals("EN_ATTENTE", findChangeRequestStatus(context.entityManagerFactory(), "commande-51")),
                    () -> assertEquals(List.of(changeRequest), notificationSpy.publishedChangeRequests())
            );
        }
    }

    @Test
    void shouldPersistPendingChangeRequestWhenNotificationPublicationFailsScenario3() {
        try (RepositoryTestContext context = modificationRepositoryUnderTest()) {
            DrinkOrderRepository repository = context.repository();
            repository.save(new DrinkOrder(
                    "commande-52",
                    "festivalier-52",
                    OrderStatus.ACQUITTEE,
                    List.of(new DrinkOrderLine("Mojito", 2))
            ));
            DrinkOrderChangeRequest changeRequest = new DrinkOrderChangeRequest(
                    "commande-52",
                    List.of(new DrinkOrderLine("Mojito", 3))
            );

            Throwable thrown = captureThrowable(() -> changeRequestRepositoryUnderTest(
                    context.entityManagerFactory(),
                    new FailingChangeRequestNotificationPublisher()
            ).save(changeRequest));

            assertAll(
                    () -> assertEquals(IllegalStateException.class, thrown == null ? null : thrown.getClass()),
                    () -> assertTrue((thrown == null ? "" : thrown.getMessage()).contains("notification publication failed")),
                    () -> assertEquals(1L, countRows(context.entityManagerFactory(), "drink_order_change_requests"))
            );
        }
    }

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

    private static RepositoryTestContext modificationRepositoryUnderTest() {
        return repositoryUnderTest();
    }

    private static DrinkOrderChangeRequestRepository changeRequestRepositoryUnderTest(
            EntityManagerFactory entityManagerFactory,
            Consumer<DrinkOrderChangeRequest> notificationPublisher
    ) {
        return new JpaDrinkOrderChangeRequestRepository(entityManagerFactory, notificationPublisher);
    }

    private static Throwable captureThrowable(Executable executable) {
        try {
            executable.execute();
            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }

    private static long countRows(EntityManagerFactory entityManagerFactory, String tableName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return ((Number) entityManager.createNativeQuery("select count(*) from " + tableName).getSingleResult()).longValue();
        } finally {
            entityManager.close();
        }
    }

    private static String findChangeRequestStatus(EntityManagerFactory entityManagerFactory, String orderId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Object result = entityManager.createNativeQuery(
                            "select status from drink_order_change_requests where order_id = ?"
                    )
                    .setParameter(1, orderId)
                    .getSingleResult();
            return result == null ? null : result.toString();
        } finally {
            entityManager.close();
        }
    }

    private record RepositoryTestContext(DrinkOrderRepository repository, EntityManagerFactory entityManagerFactory)
            implements AutoCloseable {
        @Override
        public void close() {
            entityManagerFactory.close();
        }
    }


    private static final class ChangeRequestNotificationSpy implements Consumer<DrinkOrderChangeRequest> {
        private final List<DrinkOrderChangeRequest> publishedChangeRequests = new ArrayList<>();

        @Override
        public void accept(DrinkOrderChangeRequest changeRequest) {
            publishedChangeRequests.add(changeRequest);
        }

        private List<DrinkOrderChangeRequest> publishedChangeRequests() {
            return List.copyOf(publishedChangeRequests);
        }
    }

    private static final class FailingChangeRequestNotificationPublisher implements Consumer<DrinkOrderChangeRequest> {
        @Override
        public void accept(DrinkOrderChangeRequest changeRequest) {
            throw new IllegalStateException("notification publication failed");
        }
    }
}
