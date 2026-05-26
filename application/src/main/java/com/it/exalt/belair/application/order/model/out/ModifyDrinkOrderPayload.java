package com.it.exalt.belair.application.order.model.out;

import java.util.List;

public record ModifyDrinkOrderPayload(
        String commandeId,
        String statut,
        List<ModifyDrinkOrderLinePayload> lignes
) {
}
