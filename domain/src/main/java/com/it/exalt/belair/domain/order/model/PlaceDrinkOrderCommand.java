package com.it.exalt.belair.domain.order.model;

import java.util.Map;

public record PlaceDrinkOrderCommand(
        String festivalGoerId,
        Map<String, Integer> availableStock,
        RequestedItem requestedItem
) {
}
