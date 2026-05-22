package com.it.exalt.belair.application.festivalier.usecase;

import java.util.Map;

import com.it.exalt.belair.application.festivalier.model.out.TokenBalanceResponse;

public interface ConsultTokenBalanceUseCase {
    TokenBalanceResponse handle(
            String festivalierId,
            Map<String, TokenBalanceResponse> festivalierBalances,
            boolean dailyAllocationPerformed
    );
}
