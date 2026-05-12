package com.it.exalt.belair.domain.order.port.out;

import java.util.Map;

public interface AvailableDrinkStockPort {
    boolean isAvailable(Map<String, Integer> availableStock, String articleName, int requestedQuantity);
}
