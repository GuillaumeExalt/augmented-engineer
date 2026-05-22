package com.it.exalt.belair.application.order.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderChangeRequestRepository;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

class ModifyDrinkOrderControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnUpdatedOrderWhenPatchingNonAcquittedOrderWithValidAdditionsAndRemovalsScenario1() throws Exception {
        TestContext context = new TestContext();
        context.orderRepository.seed(new DrinkOrder(
                "commande-42",
                "festivalier-42",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1))
        ));
        MockMvc mockMvc = buildMockMvc(context);

        MvcResult response = mockMvc.perform(patch("/commandes/{orderId}", "commande-42")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "ajouts", List.of(Map.of("id", "burger", "quantite", 1)),
                                "retraits", List.of(Map.of("id", "mojito", "quantite", 1)),
                                "soldeJetonsBoissonDisponibles", 3,
                                "soldeJetonsNourritureDisponibles", 2
                        ))))
                .andReturn();

        JsonNode body = readJsonBody(response);

        assertAll(
                () -> assertEquals(200, response.getResponse().getStatus()),
                () -> assertEquals("commande-42", body.path("commandeId").asText()),
                () -> assertEquals("EN_ATTENTE", body.path("statut").asText()),
                () -> assertEquals(1, quantityFor(body, "mojito")),
                () -> assertEquals(2, quantityFor(body, "burger"))
        );
    }

    @Test
    void shouldReturnAcceptedChangeRequestStatusAndNotifyBarmanWhenPatchingAcquittedOrderScenario2() throws Exception {
        TestContext context = new TestContext();
        context.orderRepository.seed(new DrinkOrder(
                "commande-43",
                "festivalier-42",
                OrderStatus.ACQUITTEE,
                List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1))
        ));
        MockMvc mockMvc = buildMockMvc(context);

        MvcResult response = mockMvc.perform(patch("/commandes/{orderId}", "commande-43")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "ajouts", List.of(Map.of("id", "burger", "quantite", 1)),
                                "retraits", List.of(Map.of("id", "mojito", "quantite", 1)),
                                "soldeJetonsBoissonDisponibles", 5,
                                "soldeJetonsNourritureDisponibles", 5
                        ))))
                .andReturn();

        JsonNode body = readJsonBody(response);

        assertAll(
                () -> assertEquals(202, response.getResponse().getStatus()),
                () -> assertEquals("commande-43", body.path("commandeId").asText()),
                () -> assertEquals("DEMANDE_CHANGEMENT_EN_ATTENTE", body.path("statut").asText()),
                () -> assertTrue(context.notificationRecorder.hasBeenNotifiedForOrder("commande-43"))
        );
    }

    @Test
    void shouldReturnValidationErrorAndKeepOriginalOrderWhenPatchingOrderBeyondAvailableBalancesScenario3() throws Exception {
        TestContext context = new TestContext();
        DrinkOrder originalOrder = new DrinkOrder(
                "commande-44",
                "festivalier-44",
                OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1))
        );
        context.orderRepository.seed(originalOrder);
        MockMvc mockMvc = buildMockMvc(context);

        MvcResult response = mockMvc.perform(patch("/commandes/{orderId}", "commande-44")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-44")
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "ajouts", List.of(Map.of("id", "mojito", "quantite", 2), Map.of("id", "burger", "quantite", 1)),
                                "retraits", List.of(),
                                "soldeJetonsBoissonDisponibles", 2,
                                "soldeJetonsNourritureDisponibles", 1
                        ))))
                .andReturn();

        JsonNode body = readJsonBody(response);

        assertAll(
                () -> assertEquals(422, response.getResponse().getStatus()),
                () -> assertEquals("SOLDE_JETONS_INSUFFISANT", body.path("message").asText()),
                () -> assertEquals(originalOrder, context.orderRepository.findById("commande-44").orElseThrow())
        );
    }

    private MockMvc buildMockMvc(TestContext context) {
        return MockMvcBuilders.standaloneSetup(new ModifyDrinkOrderController(
                context.useCase,
                context.orderRepository,
                context.notificationRecorder
        )).build();
    }

    private JsonNode readJsonBody(MvcResult response) throws Exception {
        String content = response.getResponse().getContentAsString();
        return content == null || content.isBlank() ? objectMapper.createObjectNode() : objectMapper.readTree(content);
    }

    private int quantityFor(JsonNode body, String articleId) {
        for (JsonNode article : body.path("articles")) {
            if (articleId.equals(article.path("id").asText())) {
                return article.path("quantite").asInt(Integer.MIN_VALUE);
            }
        }
        return Integer.MIN_VALUE;
    }

    private static final class TestContext {
        private final InMemoryDrinkOrderRepository orderRepository = new InMemoryDrinkOrderRepository();
        private final InMemoryDrinkOrderChangeRequestRepository changeRequestRepository =
                new InMemoryDrinkOrderChangeRequestRepository();
        private final ModifyDrinkOrderUseCase useCase = new ModifyDrinkOrderUseCase(
                orderRepository,
                changeRequestRepository
        );
        private final NotificationRecorder notificationRecorder = new NotificationRecorder();
    }

    private static final class InMemoryDrinkOrderRepository implements DrinkOrderRepository {
        private final Map<String, DrinkOrder> orders = new HashMap<>();

        private void seed(DrinkOrder order) {
            orders.put(order.orderId(), order);
        }

        @Override
        public void save(DrinkOrder order) {
            orders.put(order.orderId(), order);
        }

        @Override
        public java.util.Optional<DrinkOrder> findById(String orderId) {
            return java.util.Optional.ofNullable(orders.get(orderId));
        }

        @Override
        public void updateStatus(String orderId, OrderStatus status) {
            DrinkOrder existingOrder = orders.get(orderId);
            if (existingOrder != null) {
                orders.put(orderId, new DrinkOrder(
                        existingOrder.orderId(),
                        existingOrder.festivalierId(),
                        status,
                        existingOrder.lines()
                ));
            }
        }

        @Override
        public List<DrinkOrder> findByFestivalierIdAndStatus(String festivalierId, OrderStatus status) {
            return orders.values().stream()
                    .filter(order -> order.festivalierId().equals(festivalierId))
                    .filter(order -> order.status() == status)
                    .toList();
        }
    }

    private static final class InMemoryDrinkOrderChangeRequestRepository implements DrinkOrderChangeRequestRepository {
        @Override
        public void save(DrinkOrderChangeRequest changeRequest) {
        }
    }

    private static final class NotificationRecorder implements ModifyDrinkOrderNotificationPublisher {
        private final List<String> notifiedOrderIds = new java.util.ArrayList<>();

        @Override
        public void notifyBarman(String orderId) {
            notifiedOrderIds.add(orderId);
        }

        private boolean hasBeenNotifiedForOrder(String orderId) {
            return notifiedOrderIds.contains(orderId);
        }
    }
}
