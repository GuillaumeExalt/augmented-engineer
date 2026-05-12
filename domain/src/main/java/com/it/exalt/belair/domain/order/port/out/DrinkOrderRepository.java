package com.it.exalt.belair.domain.order.port.out;

import java.util.List;
import java.util.Optional;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.OrderStatus;

public interface DrinkOrderRepository {
    void save(DrinkOrder order);

    Optional<DrinkOrder> findById(String orderId);

    void updateStatus(String orderId, OrderStatus status);

    List<DrinkOrder> findByFestivalierIdAndStatus(String festivalierId, OrderStatus status);
}