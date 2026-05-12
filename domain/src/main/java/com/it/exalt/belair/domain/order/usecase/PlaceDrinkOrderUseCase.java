package com.it.exalt.belair.domain.order.usecase;

import com.it.exalt.belair.domain.order.model.CreatedOrder;
import com.it.exalt.belair.domain.order.model.InsufficientDrinkStockException;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.model.PlaceDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.UnknownDrinkArticleException;
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
        int requestedQuantity = command.requestedItem().quantity();
        if (requestedQuantity <= 0) {
            return new CreatedOrder("", OrderStatus.REJETEE);
        }

        String articleName = command.requestedItem().articleName();
        Integer availableQuantity = command.availableStock().get(articleName);
        if (availableQuantity == null) {
            throw new UnknownDrinkArticleException();
        }

        boolean available = availableDrinkStockPort.isAvailable(
                command.availableStock(),
                articleName,
                requestedQuantity
        );

        if (!available || availableQuantity < requestedQuantity) {
            throw new InsufficientDrinkStockException();
        }

        command.availableStock().put(articleName, availableQuantity - requestedQuantity);

        return new CreatedOrder(orderIdPort.nextOrderId(), OrderStatus.EN_ATTENTE);
    }
}
