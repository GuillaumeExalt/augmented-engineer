package com.it.exalt.belair.application.order.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.it.exalt.belair.application.order.model.in.PlaceDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.out.PlaceDrinkOrderResponse;
import com.it.exalt.belair.domain.order.port.out.AvailableDrinkStockPort;
import com.it.exalt.belair.domain.order.port.out.OrderIdPort;
import com.it.exalt.belair.domain.order.usecase.PlaceDrinkOrderUseCase;

class PlaceDrinkOrderControllerTest {

    private final PlaceDrinkOrderController controller = new PlaceDrinkOrderController(
            new PlaceDrinkOrderUseCase(
                    new InMemoryAvailableDrinkStockPort(),
                    new FixedOrderIdPort()
            )
    );
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreatePendingOrderWhenPostingAuthenticatedRequestWithAvailableItemsScenario4() throws Exception {
        PlaceDrinkOrderRequest request = new PlaceDrinkOrderRequest(
                "festivalier-42",
                List.of(new PlaceDrinkOrderRequest.RequestedArticle("mojito", 2))
        );

        MvcResult response = mockMvc.perform(post("/commandes")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .requestAttr("availableArticles", Map.of(
                                "Mojito", 10,
                                "Eau plate", 50
                        ))
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        PlaceDrinkOrderResponse body = objectMapper.readValue(
                response.getResponse().getContentAsByteArray(),
                PlaceDrinkOrderResponse.class
        );

        assertAll(
                () -> assertEquals(201, response.getResponse().getStatus()),
                () -> assertFalse(body.commandeId().isBlank()),
                () -> assertEquals("EN_ATTENTE", body.statut())
        );
    }

    @Test
    void shouldReturnUnauthorizedWhenPostingOrderWithoutAuthenticatedFestivalierScenario5() throws Exception {
        PlaceDrinkOrderRequest request = new PlaceDrinkOrderRequest(
                null,
                List.of(new PlaceDrinkOrderRequest.RequestedArticle("mojito", 1))
        );

        MvcResult response = mockMvc.perform(post("/commandes")
                        .contentType(APPLICATION_JSON)
                        .requestAttr("availableArticles", Map.of("Mojito", 10))
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        assertEquals(401, response.getResponse().getStatus());
    }

    @Test
    void shouldReturnBadRequestWhenPostingOrderWithoutRequestedArticlesScenario6() throws Exception {
        PlaceDrinkOrderRequest request = new PlaceDrinkOrderRequest("festivalier-42", List.of());

        MvcResult response = mockMvc.perform(post("/commandes")
                        .contentType(APPLICATION_JSON)
                        .header("X-Festivalier-Id", "festivalier-42")
                        .requestAttr("availableArticles", Map.of("Mojito", 10))
                        .content(objectMapper.writeValueAsBytes(request)))
                .andReturn();

        assertEquals(400, response.getResponse().getStatus());
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