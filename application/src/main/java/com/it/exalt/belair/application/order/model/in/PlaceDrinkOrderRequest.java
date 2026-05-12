package com.it.exalt.belair.application.order.model.in;

import java.util.List;

public record PlaceDrinkOrderRequest(
        String festivalierId,
        List<RequestedArticle> articles
) {
    public record RequestedArticle(String id, int quantite) {
    }
}