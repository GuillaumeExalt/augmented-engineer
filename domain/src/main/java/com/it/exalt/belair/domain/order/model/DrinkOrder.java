package com.it.exalt.belair.domain.order.model;

import java.util.List;

public record DrinkOrder(
        String orderId,
        String festivalierId,
        OrderStatus status,
        List<DrinkOrderLine> lines
) {
    public DrinkOrder {
        lines = List.copyOf(lines);
    }
}