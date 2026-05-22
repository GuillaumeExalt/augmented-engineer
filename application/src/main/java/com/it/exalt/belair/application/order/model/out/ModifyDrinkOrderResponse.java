package com.it.exalt.belair.application.order.model.out;

import java.util.List;

public record ModifyDrinkOrderResponse(
        String commandeId,
        String statut,
        List<ArticleQuantity> articles
) {
    public record ArticleQuantity(String id, int quantite) {
    }
}
