package com.it.exalt.belair.application.order.model.out;

public record ModifyDrinkOrderSuccessResponse(
        String statutTraitement,
        ModifyDrinkOrderPayload commande
) {
}
