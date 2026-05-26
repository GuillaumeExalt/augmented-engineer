package com.it.exalt.belair.application.order.model.in;

import java.util.List;

public record ModifyDrinkOrderRequest(
        String festivalierId,
        int availableDrinkTokens,
        List<ArticleDelta> ajouts,
        List<ArticleDelta> retraits
) {
    public record ArticleDelta(String article, int quantite) {
    }
}
