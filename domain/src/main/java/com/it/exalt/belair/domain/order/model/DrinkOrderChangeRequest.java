package com.it.exalt.belair.domain.order.model;

import java.util.List;

public record DrinkOrderChangeRequest(
        String orderId,
        List<DrinkOrderLine> requestedLines
) {
    public DrinkOrderChangeRequest {
        requestedLines = List.copyOf(requestedLines);
    }
}
