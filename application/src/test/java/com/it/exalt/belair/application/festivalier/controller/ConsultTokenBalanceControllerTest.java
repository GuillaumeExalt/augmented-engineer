package com.it.exalt.belair.application.festivalier.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.it.exalt.belair.application.festivalier.model.out.TokenBalanceResponse;
import com.it.exalt.belair.application.festivalier.usecase.ConsultTokenBalanceUseCase;

class ConsultTokenBalanceControllerTest {
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ConsultTokenBalanceController(new InMemoryConsultTokenBalanceUseCase()))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnPersistedFoodAndDrinkTokenBalancesWhenConsultingFestivalierBalanceScenario3() throws Exception {
        String festivalierId = "festivalier-42";

        MvcResult response = mockMvc.perform(get("/festivaliers/{festivalierId}/solde-jetons", festivalierId)
                        .accept(APPLICATION_JSON)
                        .requestAttr("festivalierBalances", Map.of(
                                festivalierId, new TokenBalanceResponse(7, 4)
                        )))
                .andReturn();

        TokenBalanceResponse body = objectMapper.readValue(
                response.getResponse().getContentAsByteArray(),
                TokenBalanceResponse.class
        );

        assertAll(
                () -> assertEquals(200, response.getResponse().getStatus()),
                () -> assertEquals(7, body.foodTokens()),
                () -> assertEquals(4, body.drinkTokens())
        );
    }

    @Test
    void shouldReturnZeroBalancesWhenConsultingInitialFestivalierBalanceScenario1() throws Exception {
        String festivalierId = "festivalier-new";

        MvcResult response = mockMvc.perform(get("/festivaliers/{festivalierId}/solde-jetons", festivalierId)
                        .accept(APPLICATION_JSON)
                        .requestAttr("festivalierBalances", Map.<String, TokenBalanceResponse>of()))
                .andReturn();

        TokenBalanceResponse body = objectMapper.readValue(
                response.getResponse().getContentAsByteArray(),
                TokenBalanceResponse.class
        );

        assertAll(
                () -> assertEquals(200, response.getResponse().getStatus()),
                () -> assertEquals(0, body.foodTokens()),
                () -> assertEquals(0, body.drinkTokens())
        );
    }

    @Test
    void shouldReturnDailyAllocatedTokenBalancesWhenConsultingBalanceAfterAllocationScenario2() throws Exception {
        String festivalierId = "festivalier-84";

        MvcResult response = mockMvc.perform(get("/festivaliers/{festivalierId}/solde-jetons", festivalierId)
                        .accept(APPLICATION_JSON)
                        .requestAttr("festivalierBalances", Map.of(
                                festivalierId, new TokenBalanceResponse(0, 0)
                        ))
                        .requestAttr("dailyAllocationPerformed", true))
                .andReturn();

        TokenBalanceResponse body = objectMapper.readValue(
                response.getResponse().getContentAsByteArray(),
                TokenBalanceResponse.class
        );

        assertAll(
                () -> assertEquals(200, response.getResponse().getStatus()),
                () -> assertEquals(9, body.foodTokens()),
                () -> assertEquals(6, body.drinkTokens())
        );
    }

    private static final class InMemoryConsultTokenBalanceUseCase implements ConsultTokenBalanceUseCase {
        private static final TokenBalanceResponse DAILY_ALLOCATION = new TokenBalanceResponse(9, 6);
        private static final TokenBalanceResponse EMPTY_BALANCE = new TokenBalanceResponse(0, 0);

        @Override
        public TokenBalanceResponse handle(
                String festivalierId,
                Map<String, TokenBalanceResponse> festivalierBalances,
                boolean dailyAllocationPerformed
        ) {
            TokenBalanceResponse snapshot = festivalierBalances.get(festivalierId);
            if (snapshot == null) {
                return EMPTY_BALANCE;
            }

            if (dailyAllocationPerformed) {
                return new TokenBalanceResponse(
                        snapshot.foodTokens() + DAILY_ALLOCATION.foodTokens(),
                        snapshot.drinkTokens() + DAILY_ALLOCATION.drinkTokens()
                );
            }

            return snapshot;
        }
    }
}
