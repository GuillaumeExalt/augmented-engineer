package com.it.exalt.belair.domain.order.model;

import java.util.List;

public record ModifyDrinkOrderCommand(
        DrinkOrder order,
        List<DrinkOrderLine> updatedLines,
        int updatedDrinkTokenCost,
        int updatedFoodTokenCost,
        int availableDrinkTokens,
        int availableFoodTokens
) {
    public ModifyDrinkOrderCommand {
        updatedLines = List.copyOf(updatedLines);
    }
}
