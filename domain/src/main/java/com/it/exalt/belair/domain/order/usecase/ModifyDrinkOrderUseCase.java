package com.it.exalt.belair.domain.order.usecase;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.InsufficientFestivalierTokenBalanceException;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.model.OrderStatus;

public final class ModifyDrinkOrderUseCase {

    public ModifyDrinkOrderResult handle(ModifyDrinkOrderCommand command) {
        if (command.order().status() == OrderStatus.ACQUITTEE) {
            return new ModifyDrinkOrderResult(
                    command.order(),
                    new DrinkOrderChangeRequest(command.order().orderId(), command.updatedLines())
            );
        }

        if (command.updatedDrinkTokenCost() > command.availableDrinkTokens()
                || command.updatedFoodTokenCost() > command.availableFoodTokens()) {
            throw new InsufficientFestivalierTokenBalanceException();
        }

        DrinkOrder updatedOrder = new DrinkOrder(
                command.order().orderId(),
                command.order().festivalierId(),
                command.order().status(),
                command.updatedLines()
        );
        return new ModifyDrinkOrderResult(updatedOrder, null);
    }
}
