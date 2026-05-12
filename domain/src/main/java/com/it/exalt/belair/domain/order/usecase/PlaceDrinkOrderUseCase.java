package com.it.exalt.belair.domain.order.usecase;

import com.it.exalt.belair.domain.order.model.CreatedOrder;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.model.PlaceDrinkOrderCommand;
import com.it.exalt.belair.domain.order.port.out.AvailableDrinkStockPort;
import com.it.exalt.belair.domain.order.port.out.OrderIdPort;

public final class PlaceDrinkOrderUseCase {
    private final AvailableDrinkStockPort availableDrinkStockPort;
    private final OrderIdPort orderIdPort;

    public PlaceDrinkOrderUseCase(
            AvailableDrinkStockPort availableDrinkStockPort,
            OrderIdPort orderIdPort
    ) {
        this.availableDrinkStockPort = availableDrinkStockPort;
        this.orderIdPort = orderIdPort;
    }

    public CreatedOrder handle(PlaceDrinkOrderCommand command) {
        boolean available = availableDrinkStockPort.isAvailable(
                command.availableStock(),
                command.requestedItem().articleName(),
                command.requestedItem().quantity()
        );

        if (!available) {
            return new CreatedOrder("", OrderStatus.REJETEE);
        }

        return new CreatedOrder(orderIdPort.nextOrderId(), OrderStatus.EN_ATTENTE);
    }
}
