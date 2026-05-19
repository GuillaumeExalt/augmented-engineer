package com.it.exalt.belair.domain.order.usecase;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.InsufficientFestivalierTokenBalanceException;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderChangeRequestRepository;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;

public final class ModifyDrinkOrderUseCase {
    private final DrinkOrderRepository drinkOrderRepository;
    private final DrinkOrderChangeRequestRepository drinkOrderChangeRequestRepository;

    public ModifyDrinkOrderUseCase(
            DrinkOrderRepository drinkOrderRepository,
            DrinkOrderChangeRequestRepository drinkOrderChangeRequestRepository
    ) {
        this.drinkOrderRepository = drinkOrderRepository;
        this.drinkOrderChangeRequestRepository = drinkOrderChangeRequestRepository;
    }

    public ModifyDrinkOrderResult handle(ModifyDrinkOrderCommand command) {
        if (command.order().status() == OrderStatus.ACQUITTEE) {
            DrinkOrderChangeRequest changeRequest = new DrinkOrderChangeRequest(
                    command.order().orderId(),
                    command.updatedLines()
            );
            drinkOrderChangeRequestRepository.save(changeRequest);
            return new ModifyDrinkOrderResult(
                    command.order(),
                    changeRequest
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
        drinkOrderRepository.save(updatedOrder);

        return new ModifyDrinkOrderResult(updatedOrder, null);
    }
}
