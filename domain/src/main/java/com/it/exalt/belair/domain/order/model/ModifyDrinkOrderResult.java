package com.it.exalt.belair.domain.order.model;

public record ModifyDrinkOrderResult(
        DrinkOrder order,
        DrinkOrderChangeRequest changeRequest
) {
    public boolean hasChangeRequest() {
        return changeRequest != null;
    }
}
