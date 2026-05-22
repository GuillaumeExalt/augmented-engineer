package com.it.exalt.belair.application.festivalier.controller;

import static java.util.Map.of;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it.exalt.belair.application.festivalier.model.out.TokenBalanceResponse;
import com.it.exalt.belair.application.festivalier.usecase.ConsultTokenBalanceUseCase;

@RestController
@RequestMapping(path = "/festivaliers/{festivalierId}/solde-jetons", produces = MediaType.APPLICATION_JSON_VALUE)
public final class ConsultTokenBalanceController {
    private final ConsultTokenBalanceUseCase useCase;

    public ConsultTokenBalanceController(ConsultTokenBalanceUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<TokenBalanceResponse> getBalance(
            @PathVariable("festivalierId") String festivalierId,
            @RequestAttribute(name = "festivalierBalances", required = false)
            Map<String, TokenBalanceResponse> festivalierBalances,
            @RequestAttribute(name = "dailyAllocationPerformed", required = false) Boolean dailyAllocationPerformed
    ) {
        return ResponseEntity.ok(useCase.handle(
                festivalierId,
                festivalierBalances == null ? of() : festivalierBalances,
                Boolean.TRUE.equals(dailyAllocationPerformed)
        ));
    }
}
