package com.it.exalt.belair.application.order.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
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
    void shouldReturnUpdatedOrderWhenPatchingNonAcquittedOrderWithValidChangesScenario1() throws Exception {
        TestContext context = new TestContext();
        context.drinkOrderRepository.store(new DrinkOrder("commande-42", "festivalier-42", OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1))));

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-42")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "ajouts", List.of(Map.of("article", "Burger", "quantite", 1)),
                                "retraits", List.of(Map.of("article", "Mojito", "quantite", 1)),
                                "soldes", Map.of("boisson", 4, "nourriture", 3),
                                "couts", Map.of("boisson", 3, "nourriture", 2)))))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(200, response.getResponse().getStatus()),
                () -> assertEquals("commande-42", body.get("commandeId")),
                () -> assertEquals("EN_ATTENTE", body.get("statut")),
                () -> assertLineQuantity(body, "Mojito", 1),
                () -> assertLineQuantity(body, "Burger", 2),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 2)),
                        context.drinkOrderRepository.findById("commande-42").orElseThrow().lines()));
    }

    @Test
    void shouldReturnPendingChangeRequestStatusAndNotifyBarmanWhenPatchingAcquittedOrderScenario2() throws Exception {
        TestContext context = new TestContext();
        context.drinkOrderRepository.store(new DrinkOrder("commande-43", "festivalier-43", OrderStatus.ACQUITTEE,
                List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1))));

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-43")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-43")
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "ajouts", List.of(Map.of("article", "Burger", "quantite", 1)),
                                "retraits", List.of(Map.of("article", "Mojito", "quantite", 1)),
                                "soldes", Map.of("boisson", 5, "nourriture", 5),
                                "couts", Map.of("boisson", 3, "nourriture", 2)))))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(202, response.getResponse().getStatus()),
                () -> assertEquals("commande-43", body.get("commandeId")),
                () -> assertEquals("DEMANDE_CHANGEMENT_EN_ATTENTE", body.get("statut")),
                () -> assertEquals(1, context.notificationSpy.invocationCount()),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 2), new DrinkOrderLine("Burger", 1)),
                        context.drinkOrderRepository.findById("commande-43").orElseThrow().lines()),
                () -> assertEquals("commande-43", context.changeRequestRepository.savedChangeRequest.orderId()),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 2)),
                        context.changeRequestRepository.savedChangeRequest.requestedLines()));
    }

    @Test
    void shouldReturnValidationErrorAndKeepOriginalOrderWhenPatchingOrderBeyondAvailableBalancesScenario3() throws Exception {
        TestContext context = new TestContext();
        context.drinkOrderRepository.store(new DrinkOrder("commande-44", "festivalier-44", OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1))));

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-44")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-44")
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "ajouts", List.of(Map.of("article", "Mojito", "quantite", 2), Map.of("article", "Burger", "quantite", 1)),
                                "retraits", List.of(),
                                "soldes", Map.of("boisson", 1, "nourriture", 1),
                                "couts", Map.of("boisson", 5, "nourriture", 3)))))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(400, response.getResponse().getStatus()),
                () -> assertEquals("SOLDE_JETONS_INSUFFISANT", body.get("message")),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1)),
                        context.drinkOrderRepository.findById("commande-44").orElseThrow().lines()),
                () -> assertEquals(0, context.notificationSpy.invocationCount()),
                () -> assertNull(context.changeRequestRepository.savedChangeRequest));
    }

    @Test
    void shouldReturnUnauthorizedWhenPatchingOrderWithoutAuthenticatedFestivalier() throws Exception {
        TestContext context = new TestContext();

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-42")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validRequestBody())))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(401, response.getResponse().getStatus()),
                () -> assertEquals("NON_AUTHENTIFIE", body.get("message")));
    }

    @Test
    void shouldReturnNotFoundWhenPatchingUnknownOrder() throws Exception {
        TestContext context = new TestContext();

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-inconnue")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .content(objectMapper.writeValueAsBytes(validRequestBody())))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(404, response.getResponse().getStatus()),
                () -> assertEquals("COMMANDE_INTROUVABLE", body.get("message")));
    }

    @Test
    void shouldReturnForbiddenWhenPatchingOrderOwnedByAnotherFestivalier() throws Exception {
        TestContext context = new TestContext();
        context.drinkOrderRepository.store(new DrinkOrder("commande-45", "festivalier-45", OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1))));

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-45")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-99")
                        .content(objectMapper.writeValueAsBytes(validRequestBody())))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(403, response.getResponse().getStatus()),
                () -> assertEquals("ACCES_INTERDIT", body.get("message")));
    }

    @Test
    void shouldReturnBadRequestWhenPatchingOrderWithMalformedLineChanges() throws Exception {
        TestContext context = new TestContext();
        context.drinkOrderRepository.store(new DrinkOrder("commande-46", "festivalier-46", OrderStatus.EN_ATTENTE,
                List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1))));

        ModifyDrinkOrderRequest request = new ModifyDrinkOrderRequest(
                List.of(new ModifyDrinkOrderRequest.LineChange(null, 1)),
                List.of(new ModifyDrinkOrderRequest.LineChange("Burger", 0)),
                new ModifyDrinkOrderRequest.TokenValues(4, 3),
                new ModifyDrinkOrderRequest.TokenValues(3, 2)
        );

        MvcResult response = buildMockMvc(context).perform(patch("/commandes/{commandeId}", "commande-46")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-46")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        Map<String, Object> body = readBody(response);
        assertAll(
                () -> assertEquals(400, response.getResponse().getStatus()),
                () -> assertEquals("LIGNE_INVALIDE: article requis et quantite strictement positive", body.get("message")),
                () -> assertEquals(List.of(new DrinkOrderLine("Mojito", 1), new DrinkOrderLine("Burger", 1)),
                        context.drinkOrderRepository.findById("commande-46").orElseThrow().lines()),
                () -> assertNull(context.changeRequestRepository.savedChangeRequest),
                () -> assertEquals(0, context.notificationSpy.invocationCount()));
    }

    private MockMvc buildMockMvc(TestContext context) {
        return MockMvcBuilders.standaloneSetup(new ModifyDrinkOrderController(
                new ModifyDrinkOrderUseCase(context.drinkOrderRepository, context.changeRequestRepository),
                context.drinkOrderRepository,
                context.notificationSpy.proxy(BarmanNotifier.class)
        )).build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readBody(MvcResult response) throws Exception {
        byte[] content = response.getResponse().getContentAsByteArray();
        return content.length == 0 ? Map.of() : objectMapper.readValue(content, Map.class);
    }

    @SuppressWarnings("unchecked")
    private void assertLineQuantity(Map<String, Object> body, String article, int quantity) {
        List<Map<String, Object>> lignes = (List<Map<String, Object>>) body.get("lignes");
        assertTrue(lignes.stream().anyMatch(line -> article.equals(line.get("article"))
                && quantity == ((Number) line.get("quantite")).intValue()));
    }

    private Map<String, Object> validRequestBody() {
        List<Map<String, Object>> ajouts = new ArrayList<>();
        ajouts.add(Map.of("article", "Burger", "quantite", 1));

        List<Map<String, Object>> retraits = new ArrayList<>();
        retraits.add(Map.of("article", "Mojito", "quantite", 1));

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("ajouts", ajouts);
        requestBody.put("retraits", retraits);
        requestBody.put("soldes", Map.of("boisson", 4, "nourriture", 3));
        requestBody.put("couts", Map.of("boisson", 3, "nourriture", 2));
        return requestBody;
    }

    private static final class TestContext {
        private final InMemoryDrinkOrderRepository drinkOrderRepository = new InMemoryDrinkOrderRepository();
        private final InMemoryDrinkOrderChangeRequestRepository changeRequestRepository = new InMemoryDrinkOrderChangeRequestRepository();
        private final NotificationSpy notificationSpy = new NotificationSpy();
    }

    private static final class InMemoryDrinkOrderRepository implements DrinkOrderRepository {
        private final Map<String, DrinkOrder> orders = new LinkedHashMap<>();

        private void store(DrinkOrder order) {
            orders.put(order.orderId(), order);
        }

        @Override
        public void save(DrinkOrder order) {
            orders.put(order.orderId(), order);
        }

        @Override
        public Optional<DrinkOrder> findById(String orderId) {
            return Optional.ofNullable(orders.get(orderId));
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

    private static final class NotificationSpy {
        private int invocationCount;

        private <T> T proxy(Class<T> interfaceType) {
            InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
                invocationCount++;
                return method.getReturnType().equals(boolean.class) ? false : null;
            };
            return interfaceType.cast(Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[] {interfaceType}, handler));
        }

        private int invocationCount() {
            return invocationCount;
        }
    }
}
