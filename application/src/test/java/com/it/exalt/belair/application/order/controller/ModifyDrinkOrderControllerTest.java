package com.it.exalt.belair.application.order.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest.ArticleDelta;
import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderChangeRequestRepository;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

class ModifyDrinkOrderControllerTest {

    private final InMemoryOrderStore orderStore = new InMemoryOrderStore();
    private final BartenderNotificationSpy bartenderNotificationSpy = new BartenderNotificationSpy();
    private final DrinkOrderRepository drinkOrderRepository = new InMemoryDrinkOrderRepository(orderStore);
    private final ModifyDrinkOrderController controller = new ModifyDrinkOrderController(
            new ModifyDrinkOrderUseCase(drinkOrderRepository, bartenderNotificationSpy),
            drinkOrderRepository
    );
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnUpdatedOrderWhenPatchingNonAcquittedOrderWithValidLineChangesScenario1() throws Exception {
        DrinkOrder existingOrder = new DrinkOrder(
                "commande-42",
                "festivalier-42",
                OrderStatus.EN_ATTENTE,
                List.of(
                        new DrinkOrderLine("Mojito", 2),
                        new DrinkOrderLine("Eau plate", 1)
                )
        );
        orderStore.save(existingOrder);
        ModifyDrinkOrderRequest request = new ModifyDrinkOrderRequest(
                "festivalier-42",
                6,
                List.of(new ArticleDelta("Mojito", 1)),
                List.of(new ArticleDelta("Eau plate", 1))
        );

        MvcResult response = mockMvc.perform(patch("/commandes/{orderId}", "commande-42")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        JsonNode body = objectMapper.readTree(response.getResponse().getContentAsByteArray());

        assertAll(
                () -> assertEquals(200, response.getResponse().getStatus()),
                () -> assertEquals("MODIFICATION_APPLIQUEE", body.path("statutTraitement").asText()),
                () -> assertEquals("commande-42", body.path("commande").path("commandeId").asText()),
                () -> assertEquals(1, body.path("commande").path("lignes").size()),
                () -> assertEquals("Mojito", body.path("commande").path("lignes").get(0).path("article").asText()),
                () -> assertEquals(3, body.path("commande").path("lignes").get(0).path("quantite").asInt())
        );
    }

    @Test
    void shouldReturnPendingChangeRequestStatusAndNotifyBartenderWhenPatchingAcquittedOrderScenario2() throws Exception {
        DrinkOrder acquittedOrder = new DrinkOrder(
                "commande-43",
                "festivalier-43",
                OrderStatus.ACQUITTEE,
                List.of(new DrinkOrderLine("Mojito", 2))
        );
        orderStore.save(acquittedOrder);
        ModifyDrinkOrderRequest request = new ModifyDrinkOrderRequest(
                "festivalier-43",
                4,
                List.of(new ArticleDelta("Mojito", 1)),
                List.of()
        );

        MvcResult response = mockMvc.perform(patch("/commandes/{orderId}", "commande-43")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-43")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        JsonNode body = objectMapper.readTree(response.getResponse().getContentAsByteArray());

        assertAll(
                () -> assertEquals(202, response.getResponse().getStatus()),
                () -> assertEquals("DEMANDE_CHANGEMENT_EN_ATTENTE", body.path("statutTraitement").asText()),
                () -> assertTrue(bartenderNotificationSpy.wasNotifiedFor("commande-43"))
        );
    }

    @Test
    void shouldReturnValidationErrorAndKeepOriginalOrderWhenPatchingWithCostExceedingBalancesScenario3() throws Exception {
        DrinkOrder existingOrder = new DrinkOrder(
                "commande-44",
                "festivalier-44",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1))
        );
        orderStore.save(existingOrder);
        ModifyDrinkOrderRequest request = new ModifyDrinkOrderRequest(
                "festivalier-44",
                1,
                List.of(new ArticleDelta("Mojito", 2)),
                List.of()
        );

        MvcResult response = mockMvc.perform(patch("/commandes/{orderId}", "commande-44")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-44")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        JsonNode body = objectMapper.readTree(response.getResponse().getContentAsByteArray());

        assertAll(
                () -> assertEquals(400, response.getResponse().getStatus()),
                () -> assertEquals("SOLDE_JETONS_INSUFFISANT", body.path("code").asText()),
                () -> assertEquals(existingOrder, orderStore.findById("commande-44"))
        );
    }

    private static final class InMemoryOrderStore {
        private final Map<String, DrinkOrder> orders = new LinkedHashMap<>();

        private void save(DrinkOrder order) {
            orders.put(order.orderId(), order);
        }

        private DrinkOrder findById(String orderId) {
            return orders.get(orderId);
        }
    }

    private static final class InMemoryDrinkOrderRepository implements DrinkOrderRepository {
        private final InMemoryOrderStore orderStore;

        private InMemoryDrinkOrderRepository(InMemoryOrderStore orderStore) {
            this.orderStore = orderStore;
        }

        @Override
        public void save(DrinkOrder order) {
            orderStore.save(order);
        }

        @Override
        public Optional<DrinkOrder> findById(String orderId) {
            return Optional.ofNullable(orderStore.findById(orderId));
        }

        @Override
        public void updateStatus(String orderId, OrderStatus status) {
            DrinkOrder existingOrder = orderStore.findById(orderId);
            if (existingOrder != null) {
                orderStore.save(new DrinkOrder(
                        existingOrder.orderId(),
                        existingOrder.festivalierId(),
                        status,
                        existingOrder.lines()
                ));
            }
        }

        @Override
        public List<DrinkOrder> findByFestivalierIdAndStatus(String festivalierId, OrderStatus status) {
            return orderStore.orders.values().stream()
                    .filter(order -> order.festivalierId().equals(festivalierId))
                    .filter(order -> order.status() == status)
                    .toList();
        }
    }

    private static final class BartenderNotificationSpy implements DrinkOrderChangeRequestRepository {
        private String notifiedOrderId;

        @Override
        public void save(DrinkOrderChangeRequest changeRequest) {
            notifiedOrderId = changeRequest.orderId();
        }

        private boolean wasNotifiedFor(String orderId) {
            return orderId.equals(notifiedOrderId);
        }
    }
}
