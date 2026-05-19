package com.it.exalt.belair.application.order.model.out;

import java.util.List;

public record ModifyDrinkOrderResponse(
        String commandeId,
        String statut,
        List<LineView> lignes,
        String message
) {
    public record LineView(String article, int quantite) {
    }
}
